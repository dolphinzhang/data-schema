package org.dol.database.utils;

import lombok.extern.slf4j.Slf4j;
import org.dol.database.schema.*;

import java.util.*;

@Slf4j
public abstract class ScriptGenerator {

    public static String generate(DatabaseSchema databaseSchema) {
        StringBuilder sbDB = new StringBuilder();
        for (TableSchema table : databaseSchema.getTables()) {
            try {
                String tableScript = tableDDL(table);
                sbDB.append(tableScript);
            } catch (Exception exception) {
                log.error("Failed to generate DDL for table: {}", table.getTableName(), exception);
            }
        }
        return sbDB.toString();
    }

    private static String tableDDL(TableSchema table) {
        StringBuilder sbTable = new StringBuilder();
        sbTable.append("CREATE TABLE `").append(table.getTableName()).append("` (\n");
        for (ColumnSchema column : table.getColumns()) {
            sbTable.append(columnDef(table, column));
        }
        KeySchema primaryKey = table.getPrimaryKey();
        if (primaryKey != null && Utils.notEmpty(primaryKey.getMemberColumns())) {
            sbTable.append("  PRIMARY KEY (");
            sbTable.append(joinNames(primaryKey.getMemberColumns()));
            sbTable.append("),\n");
        }
        List<IndexSchema> indexes = table.getIndexes();
        for (IndexSchema index : indexes) {
            if (Utils.isEmpty(index.getMemberColumns())) {
                continue;
            }
            if (index.isUnique()) {
                sbTable.append("  UNIQUE KEY `").append(index.getIndexName()).append("` (");
            } else {
                sbTable.append("  KEY `").append(index.getIndexName()).append("` (");
            }
            sbTable.append(joinNames(index.getMemberColumns()));
            sbTable.append(") USING BTREE,\n");
        }
        return sbTable.substring(0, sbTable.length() - 2) + "\n) ENGINE=InnoDB;\n\n";
    }

    private static String columnDef(TableSchema table, ColumnSchema column) {
        StringBuilder sbTable = new StringBuilder();
        sbTable.append("  `").append(column.getColumnName()).append("` ");
        appendType(sbTable, column);

        if (Utils.hasText(table.getCollation())
                && Utils.hasText(column.getCollation())
                && !column.getCollation().equalsIgnoreCase(table.getCollation())) {
            sbTable.append(" COLLATE ").append(column.getCollation());
        }
        if (column.isNotNull()) {
            sbTable.append(" NOT NULL");
        }

        if (Utils.hasLength(column.getDefaultValue())) {
            if (column.isStringColumn()) {
                String defaultValue = column.getDefaultValue();
                defaultValue = defaultValue.replaceAll("'", "''");
                sbTable.append(" DEFAULT '").append(defaultValue).append("'");
            } else {
                sbTable.append(" DEFAULT ").append(column.getDefaultValue());
            }

        }
        if (Utils.hasLength(column.getRemarks())) {
            String remarks = column.getRemarks().replaceAll("'", "''");
            sbTable.append(" COMMENT '").append(remarks).append("'");
        }
        return sbTable.append(",\n").toString();
    }

    private static void appendType(StringBuilder sbTable, ColumnSchema column) {
        DataTypeEnum typeEnum = column.getDataTypeEnum();
        if (typeEnum != null) {
            sbTable.append(typeEnum.getDataTypeName());
        } else {
            sbTable.append(column.getDataTypeName());
        }
        boolean noWidth =
                typeEnum == null
                        || typeEnum == DataTypeEnum.TEXT
                        || typeEnum == DataTypeEnum.LONGTEXT
                        || typeEnum == DataTypeEnum.TINYTEXT
                        || typeEnum == DataTypeEnum.MEDIUMTEXT
                        || typeEnum == DataTypeEnum.NTEXT
                        || typeEnum == DataTypeEnum.BLOB
                        || typeEnum == DataTypeEnum.LONGBLOB
                        || typeEnum == DataTypeEnum.MEDIUMBLOB
                        || typeEnum == DataTypeEnum.TINYBLOB
                        || typeEnum == DataTypeEnum.DATE
                        || typeEnum == DataTypeEnum.DATETIME
                        || typeEnum == DataTypeEnum.TIME
                        || typeEnum == DataTypeEnum.TIMESTAMP
                        || typeEnum == DataTypeEnum.YEAR
                        || typeEnum == DataTypeEnum.ENUM
                        || typeEnum == DataTypeEnum.SET
                        || typeEnum == DataTypeEnum.JSON
                        || typeEnum == DataTypeEnum.GEOMETRY
                        || typeEnum == DataTypeEnum.POINT
                        || typeEnum == DataTypeEnum.LINESTRING
                        || typeEnum == DataTypeEnum.POLYGON
                        || typeEnum == DataTypeEnum.MULTIPOINT
                        || typeEnum == DataTypeEnum.MULTILINESTRING
                        || typeEnum == DataTypeEnum.MULTIPOLYGON
                        || typeEnum == DataTypeEnum.GEOMETRYCOLLECTION;

        if (noWidth) {
            return;
        }

        if (typeEnum == DataTypeEnum.BIT || typeEnum == DataTypeEnum.BOOLEAN) {
            sbTable.append("(1)");
        } else if (typeEnum == DataTypeEnum.INT || typeEnum == DataTypeEnum.INTEGER || typeEnum == DataTypeEnum.BIGINT || typeEnum == DataTypeEnum.MEDIUMINT || typeEnum == DataTypeEnum.SMALLINT || typeEnum == DataTypeEnum.TINYINT) {
            sbTable.append("(").append(column.getColumnSize() + 1).append(")");
        } else if (typeEnum == DataTypeEnum.DECIMAL || typeEnum == DataTypeEnum.NUMERIC || typeEnum == DataTypeEnum.DOUBLE || typeEnum == DataTypeEnum.FLOAT || typeEnum == DataTypeEnum.REAL) {
            sbTable.append("(").append(column.getColumnSize()).append(",").append(column.getDecimalDigits()).append(")");
        } else {
            sbTable.append("(").append(column.getColumnSize()).append(")");
        }
        if (column.getUnsigned()) {
            sbTable.append(" UNSIGNED");
        }
        if (column.isAutoIncrement()) {
            sbTable.append(" AUTO_INCREMENT");
        }
    }

    private static String joinNames(List<ColumnSchema> memberColumns) {
        StringBuilder sb = new StringBuilder();
        sb.append("`").append(memberColumns.get(0).getColumnName()).append("`");
        for (int i = 1; i < memberColumns.size(); i++) {
            sb.append(",`").append(memberColumns.get(i).getColumnName()).append("`");
        }
        return sb.toString();
    }

    /**
     * 比较两个 schema 并生成 ALTER 脚本, 使 currentSchema 对齐到 targetSchema.
     *
     * @param targetSchema  目标 schema (期望的最终状态)
     * @param currentSchema 当前 schema (线上现有状态)
     * @param includeNewTable 是否为 targetSchema 中新增的表生成 CREATE TABLE
     * @param ignoreTables  忽略的表名 (支持 * 通配符, 如 "log_*")
     * @return ALTER/CREATE SQL 脚本
     */
    public static String generateModifySQL(DatabaseSchema targetSchema,
                                           DatabaseSchema currentSchema,
                                           boolean includeNewTable,
                                           String... ignoreTables) {
        StringBuilder updateScript = new StringBuilder();
        Collection<TableSchema> targetTables = targetSchema.getTables();
        Collection<TableSchema> currentTables = currentSchema.getTables();

        // 预建当前表名 Map, 避免 O(n*m) stream 查找
        Map<String, TableSchema> currentTableMap = new HashMap<>();
        for (TableSchema ct : currentTables) {
            currentTableMap.put(ct.getTableName().toLowerCase(), ct);
        }
        Map<String, TableSchema> targetTableMap = new HashMap<>();
        for (TableSchema tt : targetTables) {
            targetTableMap.put(tt.getTableName().toLowerCase(), tt);
        }

        for (TableSchema targetTable : targetTables) {
            if (ignoreTables.length > 0 && shouldIgnore(targetTable, ignoreTables)) {
                continue;
            }
            TableSchema currentTable = currentTableMap.get(targetTable.getTableName().toLowerCase());
            String tableScript = tableChangeScript(targetTable, currentTable, includeNewTable);
            if (Utils.hasText(tableScript)) {
                updateScript.append(tableScript).append("\n\n");
            }
        }

        // 报告仅存在于 currentSchema 中的表
        for (TableSchema currentTable : currentTables) {
            if (ignoreTables.length > 0 && shouldIgnore(currentTable, ignoreTables)) {
                continue;
            }
            if (!targetTableMap.containsKey(currentTable.getTableName().toLowerCase())) {
                updateScript.append("-- WARNING: table `")
                        .append(currentTable.getTableName())
                        .append("` exists in current but not in target (not dropped)\n");
            }
        }

        return updateScript.toString();
    }

    private static boolean shouldIgnore(TableSchema fromTable, String[] ignoreTables) {
        String lowerTableName = fromTable.getTableName().toLowerCase();
        for (String ignoreTable : ignoreTables) {
            ignoreTable = ignoreTable.toLowerCase();
            if (ignoreTable.equals(lowerTableName)) {
                return true;
            }
            if (ignoreTable.contains("*")) {
                ignoreTable = ignoreTable.replaceAll("\\*", "");
                if (lowerTableName.contains(ignoreTable)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String tableChangeScript(TableSchema targetTable, TableSchema currentTable, boolean includeNewTable) {
        if (currentTable == null) {
            return includeNewTable ? tableDDL(targetTable) : null;
        }
        StringBuilder sb = new StringBuilder();
        List<ColumnSchema> targetColumns = targetTable.getColumns();
        List<ColumnSchema> currentColumns = currentTable.getColumns();

        // 预建列名 Map
        Map<String, ColumnSchema> currentColMap = new HashMap<>();
        for (ColumnSchema col : currentColumns) {
            currentColMap.put(col.getColumnName().toLowerCase(), col);
        }
        Set<String> targetColNames = new HashSet<>();
        for (ColumnSchema col : targetColumns) {
            targetColNames.add(col.getColumnName().toLowerCase());
        }

        // 新增 / 修改列
        for (ColumnSchema targetCol : targetColumns) {
            ColumnSchema currentCol = currentColMap.get(targetCol.getColumnName().toLowerCase());
            String targetColDef = columnDef(targetTable, targetCol);
            if (currentCol == null) {
                sb.append("ADD COLUMN ").append(targetColDef);
            } else {
                String currentColDef = columnDef(currentTable, currentCol);
                if (!targetColDef.equalsIgnoreCase(currentColDef)) {
                    sb.append("MODIFY COLUMN ").append(targetColDef);
                }
            }
        }

        // 报告仅存在于 current 中的列
        for (ColumnSchema currentCol : currentColumns) {
            if (!targetColNames.contains(currentCol.getColumnName().toLowerCase())) {
                sb.append("-- WARNING: column `").append(currentCol.getColumnName())
                        .append("` exists in current but not in target (not dropped),\n");
            }
        }

        // 主键变更
        KeySchema targetPK = targetTable.getPrimaryKey();
        KeySchema currentPK = currentTable.getPrimaryKey();
        if (targetPK == null) {
            if (currentPK != null) {
                appendDropPrimaryKey(sb);
            }
        } else {
            if (currentPK == null) {
                appendAddPrimaryKey(sb, targetPK);
            } else if (memberChanged(targetPK.getMemberColumns(), currentPK.getMemberColumns())) {
                appendDropPrimaryKey(sb);
                appendAddPrimaryKey(sb, targetPK);
            }
        }

        // 索引变更
        List<IndexSchema> targetIndexes = targetTable.getIndexes();
        List<IndexSchema> currentIndexes = currentTable.getIndexes();
        Map<String, IndexSchema> currentIdxMap = new HashMap<>();
        for (IndexSchema idx : currentIndexes) {
            currentIdxMap.put(idx.getIndexName().toLowerCase(), idx);
        }
        Set<String> targetIdxNames = new HashSet<>();
        for (IndexSchema idx : targetIndexes) {
            targetIdxNames.add(idx.getIndexName().toLowerCase());
        }
        for (IndexSchema targetIdx : targetIndexes) {
            IndexSchema currentIdx = currentIdxMap.get(targetIdx.getIndexName().toLowerCase());
            if (currentIdx == null) {
                appendAddIndex(sb, targetIdx);
            } else if (hasChange(targetIdx, currentIdx)) {
                appendDropIndex(sb, currentIdx.getIndexName());
                appendAddIndex(sb, targetIdx);
            }
        }
        for (IndexSchema currentIdx : currentIndexes) {
            if (!targetIdxNames.contains(currentIdx.getIndexName().toLowerCase())) {
                appendDropIndex(sb, currentIdx.getIndexName());
            }
        }

        if (sb.length() == 0) {
            return null;
        }
        return "ALTER TABLE `" + targetTable.getTableName() + "`\n" + sb.substring(0, sb.length() - 2) + ";";
    }

    private static void appendDropPrimaryKey(StringBuilder sbAlterTableScript) {
        sbAlterTableScript.append("DROP PRIMARY KEY,\n");
    }

    private static void appendAddPrimaryKey(StringBuilder sbAlterTableScript, KeySchema fromPrimaryKey) {
        sbAlterTableScript.append("ADD PRIMARY KEY (")
                .append(joinNames(fromPrimaryKey.getMemberColumns()))
                .append("),\n");
    }

    private static void appendDropIndex(StringBuilder sbAlterTableScript, String indexName) {
        sbAlterTableScript.append("DROP INDEX `").append(indexName).append("`,\n");
    }

    private static void appendAddIndex(StringBuilder sbAlterTableScript, IndexSchema fromIndex) {
        if (fromIndex.isUnique()) {
            sbAlterTableScript.append("ADD UNIQUE INDEX `");
        } else {
            sbAlterTableScript.append("ADD INDEX `");
        }
        sbAlterTableScript
                .append(fromIndex.getIndexName())
                .append("` (")
                .append(joinNames(fromIndex.getMemberColumns()))
                .append(") USING BTREE,\n");
    }

    private static boolean hasChange(IndexSchema fromIndex, IndexSchema toIndex) {
        if (fromIndex.isUnique() != toIndex.isUnique()
                || fromIndex.getMemberColumns().size() != toIndex.getMemberColumns().size()) {
            return true;
        }
        return memberChanged(fromIndex.getMemberColumns(), toIndex.getMemberColumns());
    }

    private static boolean memberChanged(List<ColumnSchema> fromMemberColumns, List<ColumnSchema> toMemberColumns) {
        if (fromMemberColumns.size() != toMemberColumns.size()) {
            return true;
        }
        for (int i = 0; i < fromMemberColumns.size(); i++) {
            ColumnSchema fromCol = fromMemberColumns.get(i);
            ColumnSchema toCol = toMemberColumns.get(i);
            if (!Objects.equals(fromCol.getColumnName(), toCol.getColumnName())) {
                return true;
            }
        }
        return false;
    }

}
