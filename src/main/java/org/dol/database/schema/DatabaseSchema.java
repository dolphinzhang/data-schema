package org.dol.database.schema;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

// TODO: Auto-generated Javadoc

/**
 * 数据库Schema.
 *
 * @author dolphin
 * @date 2017年2月20日 下午4:13:20
 */
public class DatabaseSchema {

    /**
     * The table map.
     */
    private Map<String, TableSchema> tableMap;


    /**
     * Gets the tables.
     *
     * @return the tables
     */
    public Collection<TableSchema> getTables() {
        return tableMap.values();
    }

    /**
     * Sets the tables.
     *
     * @param tables the new tables
     */
    public void setTables(Collection<TableSchema> tables) {
        tableMap = new HashMap<>(tables.size());
        for (final TableSchema tableSchema : tables) {
            tableMap.put(tableSchema.getTableName().toLowerCase(), tableSchema);
        }
    }

    /**
     * Gets the table schema.
     *
     * @param tableName the table name
     * @return the table schema
     */
    public TableSchema getTableSchema(String tableName) {
        return tableMap.get(tableName.toLowerCase());
    }

    /**
     * 参照方法名.
     *
     * @param tables
     * @return
     */
    public Collection<TableSchema> getTables(String[] tables) {
        if (tables == null || tables.length == 0) {
            return getTables();
        }
        List<TableSchema> tableSchemas = new ArrayList<>(tables.length);
        for (String tableName : tables) {
            tableSchemas.add(getTableSchema(tableName));
        }
        return tableSchemas;
    }

    public Set<FieldName> fieldNames() {
        Set<FieldName> fieldNames = new TreeSet<>();
        Collection<TableSchema> tables = getTables();
        for (TableSchema tableSchema : tables) {
            List<ColumnSchema> columns = tableSchema.getColumns();
            for (ColumnSchema column : columns) {
                fieldNames.add(new FieldName(
                        column.getFieldName(),
                        column.getPropertyName(),
                        column.getDisplayName(),
                        column.getEnglishName()));
            }
        }
        return fieldNames;
    }

    public Map<String, Map<String, ColumnSchema>> getColumnMap() {
        Map<String, Map<String, ColumnSchema>> columnMap = new HashMap<>(100);
        for (TableSchema tableSchema : getTables()) {
            tableSchema.getColumns().forEach(
                    columnSchema -> {
                        Map<String, ColumnSchema> tableColumnMap = columnMap.get(columnSchema.getPropertyName());
                        if (tableColumnMap == null) {
                            tableColumnMap = new HashMap<>();
                            columnMap.put(columnSchema.getPropertyName(), tableColumnMap);
                            tableColumnMap.put(columnSchema.getTableSchema().getModelName(), columnSchema);
                        } else {
                            if (!columnSchema.getColumnName().contains("_")) {
                                tableColumnMap.put(columnSchema.getTableSchema().getModelName(), columnSchema);
                            }
                        }
                    });
        }
        return columnMap;
    }

    @Data
    @AllArgsConstructor
    public static class FieldName implements Comparable<FieldName> {
        private String fieldName;
        private String keyName;
        private String cnName;
        private String enName;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FieldName)) {
                return false;
            }
            FieldName other = (FieldName) obj;
            return this.fieldName.equals(other.fieldName);
        }

        @Override
        public int compareTo(FieldName o) {
            return this.fieldName.compareTo(o.fieldName);
        }
    }

}
