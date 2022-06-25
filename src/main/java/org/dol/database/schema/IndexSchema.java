package org.dol.database.schema;

import java.util.List;


public class IndexSchema {

    /**
     * The index name.
     */
    private String indexName;

    /**
     * The is unique.
     */
    private boolean isUnique;

    /**
     * The type.
     */
    private short type;

    /**
     * The order.
     */
    private String order;

    /**
     * The member columns.
     */
    private List<ColumnSchema> memberColumns;


    /**
     * Gets the display name and values.
     *
     * @param prefix the prefix
     * @return the display name and values
     */
    public String getDisplayNameAndValues(String prefix) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final ColumnSchema columnSchema : memberColumns) {
            stringBuilder.append(columnSchema.getDisplayName()).append("[\"+").append(prefix).append(columnSchema.getGetter()).append("()+\"]");
        }
        return stringBuilder.toString();
    }

    /**
     * Gets the display names.
     *
     * @return the display names
     */
    public String getDisplayNames() {

        final StringBuilder stringBuilder = new StringBuilder();
        for (final ColumnSchema columnSchema : memberColumns) {
            stringBuilder.append(columnSchema.getDisplayName()).append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 2);

    }

    /**
     * Gets the index name.
     *
     * @return the index name
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * Sets the index name.
     *
     * @param indexName the new index name
     */
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    /**
     * Gets the member columns.
     *
     * @return the member columns
     */
    public List<ColumnSchema> getMemberColumns() {
        return memberColumns;
    }

    /**
     * Sets the member columns.
     *
     * @param memberColumns the new member columns
     */
    public void setMemberColumns(List<ColumnSchema> memberColumns) {
        this.memberColumns = memberColumns;
    }

    /**
     * Gets the order.
     *
     * @return the order
     */
    public String getOrder() {
        return order;
    }

    /**
     * Sets the order.
     *
     * @param order the new order
     */
    public void setOrder(String order) {
        this.order = order;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public short getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(short type) {
        this.type = type;
    }

    /**
     * Checks if is unique.
     *
     * @return true, if is unique
     */
    public boolean isUnique() {
        return isUnique;
    }

    /**
     * Sets the unique.
     *
     * @param isUnique the new unique
     */
    public void setUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

}
