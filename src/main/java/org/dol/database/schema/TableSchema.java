package org.dol.database.schema;


import org.dol.database.utils.Utils;

import java.util.List;
import java.util.regex.Matcher;


public class TableSchema {

    private final String             prefix;
    /**
     * The table catalog.
     */
    private       String             tableCatalog;
    /**
     * The table name.
     */
    private       String             tableName;
    /**
     * The comment.
     */
    private       String             comment;
    /**
     * The primary column.
     */
    private       ColumnSchema       primaryColumn;
    /**
     * The status column.
     */
    private       ColumnSchema       statusColumn;
    /**
     * The primary key.
     */
    private       KeySchema          primaryKey;
    /**
     * The indexes.
     */
    private       List<IndexSchema>  indexes;
    /**
     * The is view.
     */
    private       boolean            isView;
    /**
     * The columns.
     */
    private       List<ColumnSchema> columns;
    private       ColumnSchema       createTimeColumn;
    private       ColumnSchema       createUserColumn;
    private       String             displayName;
    private       ColumnSchema       updateTimeColumn;
    private       ColumnSchema       updateUserColumn;
    private       ColumnSchema       deletedColumn;
    private       ColumnSchema       remarkColumn;
    private       String             nameWithoutPrefix;
    private String collation;

    public TableSchema(String prefix) {
        this.prefix = prefix;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets the columns.
     *
     * @return the columns
     */
    public List<ColumnSchema> getColumns() {
        return columns;
    }

    /**
     * Sets the columns.
     *
     * @param columns the new columns
     */
    public void setColumns(List<ColumnSchema> columns) {
        this.columns = columns;
    }

    /**
     * Gets the comment.
     *
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment.
     *
     * @param comment the new comment
     */
    public void setComment(String comment) {
        this.comment = comment;

    }

    /**
     * 获取创建时间列.
     *
     * @return the creates the time column
     */
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

    /**
     * 获取创建用户列.
     *
     * @return the creates the user id column
     */
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

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {

        if (displayName == null) {
            if (hasText(comment)) {
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

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the indexes.
     *
     * @return the indexes
     */
    public List<IndexSchema> getIndexes() {
        return indexes;
    }

    /**
     * Sets the indexes.
     *
     * @param indexes the new indexes
     */
    public void setIndexes(List<IndexSchema> indexes) {
        this.indexes = indexes;
    }

    /**
     * 获取模块名称.
     *
     * @return the model name
     */
    public String getModelName() {
        String tableName = nameWithoutPrefix();
        final String[] nameParts = tableName.split("_");
        final StringBuilder sb = new StringBuilder();
        for (final String namePart : nameParts) {
            sb.append(Utils.capitalize(namePart));
        }
        return sb.toString();
    }

    public String nameWithoutPrefix() {
        if (hasText(nameWithoutPrefix)) {
            return nameWithoutPrefix;
        }
        String name = getTableName();
        if (name.startsWith("t_")) {
            name = name.substring("t_".length());
        } else if (name.startsWith("v_")) {
            name = name.substring(prefix.length()) + "_view";
        } else if (hasText(prefix)) {
            if (name.startsWith(prefix)) {
                name = name.substring(prefix.length());
            }
        }
        nameWithoutPrefix = name;
        return nameWithoutPrefix;
    }

    private boolean hasText(String nameWithoutPrefix) {
        return nameWithoutPrefix != null && nameWithoutPrefix.trim().length() > 0;
    }


    /**
     * Gets the primary column.
     *
     * @return the primary column
     */
    public ColumnSchema getPrimaryColumn() {
        return primaryColumn;
    }

    /**
     * Sets the primary column.
     *
     * @param primaryColumn the new primary column
     */
    public void setPrimaryColumn(ColumnSchema primaryColumn) {
        this.primaryColumn = primaryColumn;
    }

    public String getPrimaryColumnName() {
        return primaryColumn.getPropertyName();
    }

    /**
     * Gets the primary key.
     *
     * @return the primary key
     */
    public KeySchema getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Sets the primary key.
     *
     * @param primaryKey the new primary key
     */
    public void setPrimaryKey(KeySchema primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * 获取状态列.
     *
     * @return the status column
     */
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

    /**
     * Sets the status column.
     *
     * @param statusColumn the new status column
     */
    public void setStatusColumn(ColumnSchema statusColumn) {
        this.statusColumn = statusColumn;
    }

    /**
     * Gets the table catalog.
     *
     * @return the table catalog
     */
    public String getTableCatalog() {
        return tableCatalog;
    }

    /**
     * Sets the table catalog.
     *
     * @param tableCatalog the new table catalog
     */
    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    /**
     * Gets the table name.
     *
     * @return the table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the table name.
     *
     * @param tableName the new table name
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 获取更新时间列.
     *
     * @return the update time column
     */
    public ColumnSchema getUpdateTimeColumn() {
        if (updateTimeColumn != null) {
            return updateTimeColumn;
        }

        for (final ColumnSchema columnSchema : columns) {
            if (columnSchema.isUpdateTimeColumn()) {
                updateTimeColumn = columnSchema;
                break;
            }
        }
        return updateTimeColumn;
    }

    /**
     * 获取更新用户列.
     *
     * @return the update user id column
     */
    public ColumnSchema getUpdateUserColumn() {
        if (updateUserColumn != null) {
            return updateUserColumn;
        }
        for (final ColumnSchema columnSchema : columns) {
            if (columnSchema.isUpdateUserColumn()) {
                updateUserColumn = columnSchema;
                break;
            }
        }
        return updateUserColumn;
    }

    /**
     * 是否有创建时间列.
     *
     * @return true, if successful
     */
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
        if (deletedColumn != null) {
            return deletedColumn;
        }
        for (final ColumnSchema columnSchema : columns) {
            if (columnSchema.isDeletedColumn()) {
                deletedColumn = columnSchema;
                break;
            }
        }
        return deletedColumn;
    }

    /**
     * 获取删除列.
     *
     * @return true, if successful
     */
    public boolean hasDeleteColumn() {
        return getDeletedColumn() != null;
    }

    public boolean hasRemarkColumn() {
        return getRemarkColumn() != null;
    }


    public ColumnSchema getRemarkColumn() {
        if (remarkColumn != null) {
            return remarkColumn;
        }
        for (final ColumnSchema columnSchema : columns) {
            if (columnSchema.isRemarkColumn()) {
                remarkColumn = columnSchema;
                break;
            }
        }
        return remarkColumn;
    }

    /**
     * 获取状态列.
     *
     * @return true, if successful
     */
    public boolean hasStatusColumn() {
        return getStatusColumn() != null;
    }

    /**
     * 是否有为一索引.
     *
     * @return true, if successful
     */
    public boolean hasUniqueIndex() {
        for (final IndexSchema indexSchema : indexes) {
            if (indexSchema.isUnique()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有更新时间列.
     *
     * @return true, if successful
     */
    public boolean hasUpdateTimeColumn() {
        return getUpdateTimeColumn() != null;
    }

    /**
     * 是否有更新用户列.
     *
     * @return true:表示有，反之表示无
     */
    public boolean hasUpdateUserColumn() {
        return getUpdateUserColumn() != null;
    }

    /**
     * Checks if is view.
     *
     * @return true, if is view
     */
    public boolean isView() {
        return isView;
    }

    /**
     * Sets the view.
     *
     * @param isView the new view
     */
    public void setView(boolean isView) {
        this.isView = isView;
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
