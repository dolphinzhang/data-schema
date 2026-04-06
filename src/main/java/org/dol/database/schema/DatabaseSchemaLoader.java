package org.dol.database.schema;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dol.database.utils.Utils;

import java.sql.*;
import java.util.*;

@Slf4j
public class DatabaseSchemaLoader {

    private DatabaseSchemaLoader() {
    }

    private static List<IndexSchema> getIndexes(Connection connection, String catalog, String schema, TableSchema tableSchema) {
        try {
            final DatabaseMetaData databaseMetaData = connection.getMetaData();
            try (ResultSet rs = databaseMetaData.getIndexInfo(
                    catalog, schema, tableSchema.getTableName(), false, false)) {

                final List<IndexSchema> indexSchemas = new ArrayList<>();
                final Map<String, IndexSchema> indexColumns = new HashMap<>();
                addIndex(tableSchema, rs, indexColumns, indexSchemas);
                return indexSchemas;
            }
        } catch (Exception ex) {
            log.error("get table indexes fail", ex);
            return Collections.emptyList();
        }
    }

    private static void addIndex(TableSchema tableSchema, ResultSet rs, Map<String, IndexSchema> indexColumns, List<IndexSchema> indexSchemas) throws SQLException {
        // 预建列名 → ColumnSchema 映射, 避免每个索引列都线性扫描
        Map<String, ColumnSchema> columnByName = new HashMap<>();
        for (ColumnSchema col : tableSchema.getColumns()) {
            columnByName.put(col.getColumnName().toUpperCase(), col);
        }
        while (rs.next()) {
            final String indexName = rs.getString("INDEX_NAME");
            if (indexName == null || indexName.equalsIgnoreCase("PRIMARY")) {
                continue;
            }
            final String columnName = rs.getString("COLUMN_NAME");
            IndexSchema indexSchema;
            if (!indexColumns.containsKey(indexName)) {
                indexSchema = new IndexSchema();
                indexSchema.setIndexName(indexName);
                indexSchema.setUnique(!rs.getBoolean("NON_UNIQUE"));
                indexSchema.setType(rs.getShort("TYPE"));
                indexSchema.setOrder(rs.getString("ASC_OR_DESC"));
                indexSchema.setMemberColumns(new ArrayList<>());
                indexSchemas.add(indexSchema);
                indexColumns.put(indexName, indexSchema);
            } else {
                indexSchema = indexColumns.get(indexName);
            }
            ColumnSchema column = columnByName.get(columnName.toUpperCase());
            if (column != null) {
                indexSchema.getMemberColumns().add(column);
            }
        }
    }

    private static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (final Exception e) {
            log.error("close connection", e);
        }
    }

    private static List<ColumnSchema> getColumns(Connection connection,
                                                  String catalog,
                                                  String schema,
                                                  TableSchema tableSchema,
                                                  Map<String, Map<String, Object>> columnDefs) throws SQLException {
        final DatabaseMetaData databaseMetaData = connection.getMetaData();
        final List<ColumnSchema> columnSchemas = new ArrayList<>();

        try (ResultSet rs = databaseMetaData.getColumns(catalog, schema, tableSchema.getTableName(), "%")) {
            while (rs.next()) {
                final String columnName = rs.getString("COLUMN_NAME");
                final int dataType = rs.getInt("DATA_TYPE");
                final String rawTypeName = rs.getString("TYPE_NAME");
                String dataTypeName = rawTypeName.split("\\s")[0];

                final int columnSize = rs.getInt("COLUMN_SIZE");
                final int decimalDigits = rs.getInt("DECIMAL_DIGITS");
                final int nullAble = rs.getInt("NULLABLE");
                final String remarks = rs.getString("REMARKS");
                final String defaultValue = rs.getString("COLUMN_DEF");
                final String isAutoincrement = rs.getString("IS_AUTOINCREMENT");

                final ColumnSchema columnSchema = new ColumnSchema();
                columnSchema.setTableSchema(tableSchema);
                columnSchema.setColumnName(columnName);
                columnSchema.setRemarks(remarks);
                columnSchema.setColumnSize(columnSize);
                columnSchema.setDataType(dataType);
                columnSchema.setDataTypeName(dataTypeName);
                columnSchema.setAutoIncrement("YES".equalsIgnoreCase(isAutoincrement));
                columnSchema.setDecimalDigits(decimalDigits);
                columnSchema.setDefaultValue(defaultValue);
                columnSchema.setNullable(nullAble == 1);
                if (rawTypeName.contains("UNSIGNED")) {
                    columnSchema.setUnsigned(true);
                }
                if (!columnDefs.isEmpty()) {
                    Map<String, Object> colDef = columnDefs.get(columnName);
                    if (colDef != null) {
                        columnSchema.setCharacterSet((String) colDef.get("CHARACTER_SET_NAME"));
                        columnSchema.setCollation((String) colDef.get("COLLATION_NAME"));
                    }
                }
                columnSchemas.add(columnSchema);
            }
        }
        return columnSchemas;
    }

    private static Map<String, Map<String, Object>> getTableDefFromDB(Connection connection, String catalog) {
        Map<String, Map<String, Object>> tableDef = new HashMap<>();
        String sql = "SELECT * from information_schema.`TABLES` s where s.table_schema=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, catalog);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> table = new HashMap<>();
                    table.put("TABLE_COLLATION", resultSet.getString("TABLE_COLLATION"));
                    tableDef.put(resultSet.getString("TABLE_NAME"), table);
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to load table definitions from information_schema", ex);
        }
        return tableDef;
    }

    /**
     * 一次查询加载整个 catalog 的所有列字符集/排序规则信息.
     * 返回结构: tableName -> columnName -> {CHARACTER_SET_NAME, COLLATION_NAME}
     */
    private static Map<String, Map<String, Map<String, Object>>> getAllColumnDefsFromDB(Connection connection, String catalog) {
        Map<String, Map<String, Map<String, Object>>> result = new HashMap<>();
        String sql = "SELECT TABLE_NAME,COLUMN_NAME,CHARACTER_SET_NAME,COLLATION_NAME from information_schema.COLUMNS where TABLE_SCHEMA=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, catalog);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    String columnName = rs.getString("COLUMN_NAME");
                    Map<String, Object> colDef = new HashMap<>(2);
                    colDef.put("CHARACTER_SET_NAME", rs.getString("CHARACTER_SET_NAME"));
                    colDef.put("COLLATION_NAME", rs.getString("COLLATION_NAME"));
                    result.computeIfAbsent(tableName, k -> new HashMap<>()).put(columnName, colDef);
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to load column definitions from information_schema", ex);
        }
        return result;
    }

    @SneakyThrows
    public static DatabaseSchema load(String driverClassName,
                                      String jdbcUrl,
                                      String userName,
                                      String password,
                                      String tablePrefix) {
        return load(driverClassName, jdbcUrl, userName, password, tablePrefix, false);
    }

    @SneakyThrows
    public static DatabaseSchema load(String driverClassName,
                                      String jdbcUrl,
                                      String userName,
                                      String password,
                                      String tablePrefix,
                                      boolean loadFromDb) {
        return load(driverClassName, jdbcUrl, userName, password, null, null, tablePrefix, loadFromDb);
    }

    @SneakyThrows
    public static DatabaseSchema load(Connection connection, String tablePrefix, boolean loadFromDb) {
        return load(connection, null, null, tablePrefix, loadFromDb);
    }

    @SneakyThrows
    public static DatabaseSchema load(Connection connection, String tablePrefix) {
        return load(connection, tablePrefix, false);
    }

    private static KeySchema getPrimaryKey(Connection connection,
                                           String catalog,
                                           String schema,
                                           TableSchema tableSchema) throws Exception {
        final DatabaseMetaData databaseMetaData = connection.getMetaData();
        final KeySchema keySchema = new KeySchema();
        final List<ColumnSchema> memberColumns = new ArrayList<>();
        keySchema.setMemberColumns(memberColumns);

        Map<String, ColumnSchema> columnByName = new HashMap<>();
        for (ColumnSchema col : tableSchema.getColumns()) {
            columnByName.put(col.getColumnName().toUpperCase(), col);
        }

        try (ResultSet rs = databaseMetaData.getPrimaryKeys(catalog, schema, tableSchema.getTableName())) {
            while (rs.next()) {
                final String columnName = rs.getString("COLUMN_NAME");
                ColumnSchema column = columnByName.get(columnName.toUpperCase());
                if (column != null) {
                    column.setPrimary(true);
                    tableSchema.setPrimaryColumn(column);
                    memberColumns.add(column);
                }
                keySchema.setKeyName(rs.getString("PK_NAME"));
            }
        }
        return keySchema;
    }

    @SneakyThrows
    public static DatabaseSchema load(String driverClassName,
                                      String jdbcUrl,
                                      String userName,
                                      String password,
                                      String catalog,
                                      String schema,
                                      String tablePrefix,
                                      boolean loadFromDb) {
        Connection connection = null;
        try {
            connection = getConnection(driverClassName, jdbcUrl, userName, password);
            return load(connection, catalog, schema, tablePrefix, loadFromDb);
        } finally {
            closeConnection(connection);
        }
    }

    public static DatabaseSchema load(Connection connection,
                                      String catalog,
                                      String schema,
                                      String tablePrefix,
                                      boolean loadFromDb) throws Exception {
        if (Utils.isEmpty(catalog)) {
            catalog = connection.getCatalog();
        }
        if (Utils.isEmpty(schema)) {
            try {
                schema = connection.getSchema();
            } catch (Exception ignore) {
            }
        }
        if (Utils.isEmpty(schema)) {
            schema = catalog;
        }

        DatabaseSchema databaseSchema = new DatabaseSchema();
        final DatabaseMetaData databaseMetaData = connection.getMetaData();
        final String[] types = {"table", "view"};
        final List<TableSchema> tableSchemas = new ArrayList<>();

        // 批量预加载: 1 条 SQL 获取所有表定义, 1 条 SQL 获取所有列字符集 (代替 N+1 查询)
        Map<String, Map<String, Object>> tableDefs = loadFromDb ? getTableDefFromDB(connection, catalog) : Collections.emptyMap();
        Map<String, Map<String, Map<String, Object>>> allColumnDefs = loadFromDb ? getAllColumnDefsFromDB(connection, catalog) : Collections.emptyMap();
        Map<String, String> tableComments = getTableComments(connection, catalog);

        try (ResultSet rs = databaseMetaData.getTables(catalog, schema, null, types)) {
            while (rs.next()) {
                final TableSchema tableSchema = new TableSchema(tablePrefix);
                tableSchema.setTableCatalog(rs.getString("TABLE_CAT"));
                tableSchema.setTableName(rs.getString("TABLE_NAME"));
                tableSchema.setComment(rs.getString("REMARKS"));
                Map<String, Map<String, Object>> columnDefs = allColumnDefs.getOrDefault(tableSchema.getTableName(), Collections.emptyMap());
                final List<ColumnSchema> columnSchemas = getColumns(connection, catalog, schema, tableSchema, columnDefs);
                tableSchema.setColumns(columnSchemas);
                tableSchema.setIndexes(getIndexes(connection, catalog, schema, tableSchema));
                tableSchema.setPrimaryKey(getPrimaryKey(connection, catalog, schema, tableSchema));
                tableSchema.setView(rs.getString(4).equals("VIEW"));
                if (tableSchema.isTable() && !tableDefs.isEmpty()) {
                    Map<String, Object> td = tableDefs.get(tableSchema.getTableName());
                    if (td != null) {
                        tableSchema.setCollation((String) td.get("TABLE_COLLATION"));
                    }
                }
                // 补充 comment (从批量查询结果)
                if (!Utils.hasText(tableSchema.getComment())) {
                    tableSchema.setComment(tableComments.getOrDefault(tableSchema.getTableName(), ""));
                }
                tableSchemas.add(tableSchema);
            }
        }
        databaseSchema.setTables(tableSchemas);
        return databaseSchema;
    }

    private static Connection getConnection(String driverClassName,
                                            String jdbcUrl,
                                            String userName,
                                            String password) {
        try {
            Class.forName(driverClassName);
            return DriverManager.getConnection(jdbcUrl, userName, password);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 一次查询加载整个 catalog 的所有表注释.
     * 替代原来的 N 条 SHOW CREATE TABLE 查询.
     */
    private static Map<String, String> getTableComments(Connection connection, String catalog) {
        Map<String, String> comments = new HashMap<>();
        String sql = "SELECT TABLE_NAME, TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, catalog);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String comment = rs.getString("TABLE_COMMENT");
                    if (Utils.hasText(comment)) {
                        comments.put(rs.getString("TABLE_NAME"), comment);
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to load table comments from information_schema", ex);
        }
        return comments;
    }

}
