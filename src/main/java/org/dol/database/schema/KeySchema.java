package org.dol.database.schema;

import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * 主键Schema.
 *
 * @author dolphin
 * @date 2017年2月20日 下午4:14:13
 */
public class KeySchema {

    /**
     * The key name.
     */
    private String keyName;

    /**
     * The member columns.
     */
    private List<ColumnSchema> memberColumns;

    /**
     * Gets the key name.
     *
     * @return the key name
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Sets the key name.
     *
     * @param keyName the new key name
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
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
}
