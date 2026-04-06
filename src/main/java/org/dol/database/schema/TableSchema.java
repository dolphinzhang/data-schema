package org.dol.database.schema;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.dol.database.utils.Utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;


@Getter
@Setter
public class TableSchema {

    private final String prefix;
    private String tableCatalog;
    private String tableName;
    private String comment;
    private ColumnSchema primaryColumn;
    private ColumnSchema statusColumn;
    private KeySchema primaryKey;
    private List<IndexSchema> indexes;
    private boolean isView;
    private List<ColumnSchema> columns;
    private ColumnSchema createTimeColumn;
    private ColumnSchema createUserColumn;
    private String displayName;
    private ColumnSchema updateTimeColumn;
    private ColumnSchema updateUserColumn;
    private ColumnSchema deletedColumn;
    private ColumnSchema remarkColumn;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private String nameWithoutPrefix;
    private String collation;

    public TableSchema(String prefix) {
        this.prefix = prefix;
    }

    public ColumnSchema getCreateTimeColumn() {
        if (createTimeColumn == null) {
            for (final ColumnSchema columnSchema : columns) {
                if (columnSchema.isCreateTimeColumn()) {
                    createTimeColumn = columnSchema;
                    break;
                }
            }
        }
        return createTimeColumn;
    }

    public ColumnSchema getColumn(String columnName) {
        return columns.stream().filter(c -> c.getColumnName().equalsIgnoreCase(columnName))
                .findFirst().orElse(null);
    }

    public boolean hasColumn(String columnName) {
        return columns.stream().anyMatch(c -> c.getColumnName().equalsIgnoreCase(columnName));
    }

    public ColumnSchema getCreateUserColumn() {
        if (createUserColumn == null) {
            for (final ColumnSchema columnSchema : columns) {
                if (columnSchema.isCreateUserColumn()) {
                    createUserColumn = columnSchema;
                    break;
                }
            }
        }
        return createUserColumn;
    }

    public String getDisplayName() {
        if (displayName == null) {
            if (Utils.hasText(comment)) {
                displayName = comment;
                Matcher matcher = SchemaConstraints.SYMBOL_PATTERN.matcher(displayName);
                if (matcher.find()) {
                    displayName = displayName.substring(0, matcher.start());
                }
            } else {
                displayName = getModelName();
            }
        }
        return displayName;
    }

    public String getModelName() {
        String name = nameWithoutPrefix();
        final String[] nameParts = name.split("_");
        final StringBuilder sb = new StringBuilder();
        for (final String namePart : nameParts) {
            sb.append(Utils.capitalize(namePart));
        }
        return sb.toString();
    }

    public String nameWithoutPrefix() {
        if (Utils.hasText(nameWithoutPrefix)) {
            return nameWithoutPrefix;
        }
        String name = getTableName();
        if (Utils.hasText(prefix) && name.startsWith(prefix)) {
            name = name.substring(prefix.length());
            if (prefix.equals("v_") || prefix.equals("V_")) {
                name = name + "_view";
            }
        } else if (name.startsWith("t_")) {
            name = name.substring("t_".length());
        } else if (name.startsWith("v_")) {
            name = name.substring("v_".length()) + "_view";
        }
        nameWithoutPrefix = name;
        return nameWithoutPrefix;
    }

    public String getPrimaryColumnName() {
        return primaryColumn != null ? primaryColumn.getPropertyName() : null;
    }

    public ColumnSchema getStatusColumn() {
        if (statusColumn == null) {
            for (final ColumnSchema columnSchema : columns) {
                if (columnSchema.isStatusColumn()) {
                    statusColumn = columnSchema;
                    break;
                }
            }
        }
        return statusColumn;
    }

    public ColumnSchema getDeleteUserColumn() {
        return columns.stream().filter(ColumnSchema::isDeleteUserColumn)
                .findFirst().orElse(null);
    }

    public ColumnSchema getDeleteTimeColumn() {
        return columns.stream().filter(ColumnSchema::isDeleteTimeColumn)
                .findFirst().orElse(null);
    }

    public boolean hasDeleteUserColumn() {
        return columns.stream().anyMatch(ColumnSchema::isDeleteUserColumn);
    }

    public boolean hasDeleteTimeColumn() {
        return columns.stream().anyMatch(ColumnSchema::isDeleteTimeColumn);
    }

    public ColumnSchema getUpdateTimeColumn() {
        if (updateTimeColumn == null) {
            for (final ColumnSchema columnSchema : columns) {
                if (columnSchema.isUpdateTimeColumn()) {
                    updateTimeColumn = columnSchema;
                    break;
                }
            }
        }
        return updateTimeColumn;
    }

    public boolean hasKeywordColumn() {
        return this.columns.stream().anyMatch(ColumnSchema::isKeywordColumn);
    }

    public List<ColumnSchema> getKeywordColumns() {
        return this.columns.stream().filter(ColumnSchema::isKeywordColumn).collect(Collectors.toList());
    }

    public ColumnSchema getUpdateUserColumn() {
        if (updateUserColumn == null) {
            for (final ColumnSchema columnSchema : columns) {
                if (columnSchema.isUpdateUserColumn()) {
                    updateUserColumn = columnSchema;
                    break;
                }
            }
        }
        return updateUserColumn;
    }

    public boolean hasCreateColumn() {
        return hasCreateTimeColumn();
    }

    public boolean hasCreateTimeColumn() {
        return getCreateTimeColumn() != null;
    }

    public boolean hasCreateUserColumn() {
        return getCreateUserColumn() != null;
    }

    public ColumnSchema getDeletedColumn() {
        if (deletedColumn == null) {
            for (final ColumnSchema columnSchema : columns) {
                if (columnSchema.isDeleteColumn()) {
                    deletedColumn = columnSchema;
                    break;
                }
            }
        }
        return deletedColumn;
    }

    public boolean hasDeleteColumn() {
        return getDeletedColumn() != null;
    }

    public boolean hasRemarkColumn() {
        return getRemarkColumn() != null;
    }

    public ColumnSchema getRemarkColumn() {
        if (remarkColumn == null) {
            for (final ColumnSchema columnSchema : columns) {
                if (columnSchema.isRemarkColumn()) {
                    remarkColumn = columnSchema;
                    break;
                }
            }
        }
        return remarkColumn;
    }

    public boolean hasStatusColumn() {
        return getStatusColumn() != null;
    }

    public boolean hasUniqueIndex() {
        for (final IndexSchema indexSchema : indexes) {
            if (indexSchema.isUnique()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasUpdateTimeColumn() {
        return getUpdateTimeColumn() != null;
    }

    public boolean hasUpdateUserColumn() {
        return getUpdateUserColumn() != null;
    }

    public boolean isTable() {
        return !isView;
    }

    public boolean hasIdColumn() {
        return getIdColumn() != null;
    }

    public ColumnSchema getIdColumn() {
        if (this.primaryColumn != null && this.primaryColumn.getColumnName().equalsIgnoreCase("id")) {
            return this.primaryColumn;
        }
        return null;
    }
}
