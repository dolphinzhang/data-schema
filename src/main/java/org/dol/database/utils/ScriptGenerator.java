package org.dol.database.utils;

import org.dol.database.schema.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class ScriptGenerator {

    public static String generate(DatabaseSchema databaseSchema) {
        StringBuilder sbDB = new StringBuilder();
        for (TableSchema table : databaseSchema.getTables()) {
            String tableScript = tableDDL(table);
            sbDB.append(tableScript);
        }
        return sbDB.toString();
    }

    private static String tableDDL(TableSchema table) {
        StringBuilder sbTable = new StringBuilder();
        sbTable.append("CREATE TABLE `").append(table.getTableName()).append("` (\n");
        for (ColumnSchema column : table.getColumns()) {
            sbTable.append(columnDef(column));
        }
        KeySchema primaryKey = table.getPrimaryKey();
        if (primaryKey != null) {
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
        return sbTable.substring(0, sbTable.length() - 2) + "\n) ENGINE=InnoDB DEFAULT CHARSET=utf8;\n\n";
    }

    private static String columnDef(ColumnSchema column) {
        StringBuilder sbTable = new StringBuilder();
        sbTable.append("  `").append(column.getColumnName()).append("` ");
        appendType(sbTable, column);
        if (column.isNotNull()) {
            sbTable.append(" NOT NULL");
        }
        if (Utils.hasLength(column.getDefaultValue())) {
            sbTable.append(" DEFAULT ").append(column.getDefaultValue());
        }
        if (Utils.hasLength(column.getRemarks())) {
            sbTable.append(" COMMENT '").append(column.getRemarks()).append("'");
        }
        return sbTable.append(",\n").toString();
    }

    private static void appendType(StringBuilder sbTable, ColumnSchema column) {
        DataTypeEnum typeEnum = column.getDataTypeEnum();
        sbTable.append(typeEnum.getDataTypeName());

        boolean noWidth =
                typeEnum == DataTypeEnum.TEXT
                        || typeEnum == DataTypeEnum.LONGTEXT
                        || typeEnum == DataTypeEnum.TINYTEXT
                        || typeEnum == DataTypeEnum.MEDIUMTEXT
                        || typeEnum == DataTypeEnum.NTEXT
                        || typeEnum == DataTypeEnum.BLOB
                        || typeEnum == DataTypeEnum.LONGBLOB
                        || typeEnum == DataTypeEnum.MEDIUMBLOB
                        || typeEnum == DataTypeEnum.TINYBLOB
                        || typeEnum == DataTypeEnum.DATE
                        || typeEnum == DataTypeEnum.TIMESTAMP
                        || typeEnum == DataTypeEnum.YEAR;

        if (noWidth) {
            return;
        }

        if (typeEnum == DataTypeEnum.BIT || typeEnum == DataTypeEnum.BOOLEAN) {
            sbTable.append("(1)");
        } else if (typeEnum == DataTypeEnum.INT || typeEnum == DataTypeEnum.BIGINT || typeEnum == DataTypeEnum.MEDIUMINT || typeEnum == DataTypeEnum.SMALLINT || typeEnum == DataTypeEnum.TINYINT) {
            sbTable.append("(").append(column.getColumnSize() + 1).append(")");
        } else if (typeEnum == DataTypeEnum.DECIMAL || typeEnum == DataTypeEnum.DOUBLE || typeEnum == DataTypeEnum.FLOAT || typeEnum == DataTypeEnum.REAL) {
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

    public static String generateModifySQL(DatabaseSchema fromDB, DatabaseSchema toDB) {
        StringBuilder updateScript = new StringBuilder();
        Collection<TableSchema> fromTables = fromDB.getTables();
        Collection<TableSchema> toTables = toDB.getTables();
        for (TableSchema fromTable : fromTables) {
            TableSchema toTable = toTables.stream().filter(ot -> sameTable(ot, fromTable)).findFirst().orElse(null);
            String tableScript = tableChangeScript(fromTable, toTable);
            if (Utils.hasText(tableScript)) {
                updateScript.append(tableScript).append("\n\n");
            }
        }
        return updateScript.toString();
    }

    private static String tableChangeScript(TableSchema fromTable, TableSchema toTable) {
        if (toTable == null) {
            return tableDDL(fromTable);
        } else {
            StringBuilder sbAlterTableScript = new StringBuilder();
            List<ColumnSchema> fromColumns = fromTable.getColumns();
            List<ColumnSchema> toColumns = toTable.getColumns();
            for (ColumnSchema fromColumn : fromColumns) {
                ColumnSchema toColumn = toColumns.stream()
                        .filter(col -> col.getColumnName().equalsIgnoreCase(fromColumn.getColumnName()))
                        .findFirst().orElse(null);
                String fromColumnDef = columnDef(fromColumn);
                if (toColumn == null) {
                    sbAlterTableScript.append("ADD COLUMN ").append(fromColumnDef);
                } else {
                    String toColumnDef = columnDef(toColumn);
                    if (!fromColumnDef.equalsIgnoreCase(toColumnDef)) {
                        sbAlterTableScript.append("MODIFY COLUMN ").append(fromColumnDef);
                    }
                }
            }
            KeySchema fromPrimaryKey = fromTable.getPrimaryKey();
            KeySchema toPrimaryKey = toTable.getPrimaryKey();
            if (fromPrimaryKey == null) {
                if (toPrimaryKey != null) {
                    appendDropPrimaryKey(sbAlterTableScript);
                }
            } else {
                if (toPrimaryKey == null) {
                    appendAddPrimaryKey(sbAlterTableScript, fromPrimaryKey);
                } else if (memberChanged(fromPrimaryKey.getMemberColumns(), toPrimaryKey.getMemberColumns())) {
                    appendDropPrimaryKey(sbAlterTableScript);
                    appendAddPrimaryKey(sbAlterTableScript, fromPrimaryKey);
                }
            }
            List<IndexSchema> fromIndexes = fromTable.getIndexes();
            List<IndexSchema> toIndexes = toTable.getIndexes();
            for (IndexSchema fromIndex : fromIndexes) {
                IndexSchema toIndex = toIndexes.stream()
                        .filter(idx -> fromIndex.getIndexName().equalsIgnoreCase(idx.getIndexName()))
                        .findFirst()
                        .orElse(null);
                if (toIndex == null) {
                    appendAddIndex(sbAlterTableScript, fromIndex);
                } else if (hasChange(fromIndex, toIndex)) {
                    appendDropIndex(sbAlterTableScript, toIndex.getIndexName());
                    appendAddIndex(sbAlterTableScript, fromIndex);
                }
            }
            for (IndexSchema toIndex : toIndexes) {
                IndexSchema fromIndex = fromIndexes.stream()
                        .filter(idx -> toIndex.getIndexName().equalsIgnoreCase(idx.getIndexName()))
                        .findFirst()
                        .orElse(null);
                if (fromIndex == null) {
                    appendDropIndex(sbAlterTableScript, toIndex.getIndexName());
                }
            }
            if (sbAlterTableScript.length() == 0) {
                return null;
            }
            return "ALTER TABLE `" + fromTable.getTableName() + "`\n" + sbAlterTableScript.substring(0, sbAlterTableScript.length() - 2) + ";";
        }
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

    private static boolean memberChanged(List<ColumnSchema> fromMemeberColumns, List<ColumnSchema> toMemeberColumns) {
        for (int i = 0; i < fromMemeberColumns.size(); i++) {
            ColumnSchema fromIndexCol = fromMemeberColumns.get(i);
            ColumnSchema toIndexCol = toMemeberColumns.get(i);
            if (!Objects.equals(fromIndexCol.getColumnName(), toIndexCol.getColumnName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean sameTable(TableSchema ot, TableSchema table) {
        return ot.getTableName().equalsIgnoreCase(table.getTableName());
    }

}
