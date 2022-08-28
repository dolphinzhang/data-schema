package org.dol.database.schema;


import org.dol.database.utils.Utils;

import java.util.regex.Matcher;


public class ColumnSchema {

    /**
     * The table schema.
     */
    private TableSchema tableSchema;

    /**
     * The column name.
     */
    private String columnName;

    /**
     * The column size.
     */
    private int columnSize;

    /**
     * The decimal digits.
     */
    private int decimalDigits;

    /**
     * The nullable.
     */
    private boolean nullable;

    /**
     * The remarks.
     */
    private String remarks;

    /**
     * The default value.
     */
    private String defaultValue;

    /**
     * The is autoincrement.
     */
    private String isAutoincrement;

    /**
     * The is primary.
     */
    private boolean isPrimary = false;

    /**
     * The is auto increment.
     */
    private boolean isAutoIncrement = false;

    /**
     * The property name.
     */
    private String propertyName;

    /**
     * The display name.
     */
    private String displayName;

    /**
     * The data type.
     */
    private int          dataType;
    private Boolean      unsigned        = false;
    /**
     * The data type name.
     */
    private String       dataTypeName;
    /**
     * The jdbc type.
     */
    private String       jdbcType;
    /**
     * The java type.
     */
    private String       javaType;
    private String       csType;
    /**
     * The full java type.
     */
    private String       fullJavaType;
    /**
     * The property var name.
     */
    private String       propertyVarName;
    /**
     * The upper property name.
     */
    private String       capitalizePropertyName;
    /**
     * The data type enum.
     */
    private DataTypeEnum dataTypeEnum;
    /**
     * The getter.
     */
    private String       getter;
    /**
     * The setter.
     */
    private String       setter;
    private Boolean      isStatusColumn  = false;
    private Boolean      isDeletedColumn = false;
    private String       csPropertyName;

    public String getCharacterSet() {
        return characterSet;
    }

    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    private String characterSet;
    private String collation;

    public Boolean getUnsigned() {
        return unsigned;
    }

    public void setUnsigned(Boolean unsigned) {
        this.unsigned = unsigned;
    }

    /**
     * Gets the column length.
     *
     * @return the column length
     */
    public Double getColumnLength() {
        Double double1 = (double) columnSize;
        if (decimalDigits > 0) {
            double1 = new Double(columnSize + "." + decimalDigits);
        }
        return double1;
    }

    /**
     * Gets the column name.
     *
     * @return the column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets the column name.
     *
     * @param columnName the new column name
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
        final String[] columnNameParts = columnName.split("_");
        final StringBuilder sbJava = new StringBuilder();
        final StringBuilder sbCS = new StringBuilder();
        for (final String namePart : columnNameParts) {
            sbJava.append(Utils.capitalize(namePart));
            sbCS.append(Utils.capitalize(namePart));
        }
        final String propertyFieldName = Utils.uncapitalize(sbJava.toString());
        setPropertyName(propertyFieldName);
        setPropertyVarName(propertyFieldName);
        this.csPropertyName = sbCS.toString();
    }

    public String getFieldName() {
        if (columnName.indexOf("_") > 0) {
            return columnName.toUpperCase();
        } else {
            return (tableSchema.nameWithoutPrefix() + "_" + columnName).toUpperCase();
        }
    }

    public boolean isIdColumn() {
        return this.tableSchema.getIdColumn() == this;
    }

    /**
     * Gets the column size.
     *
     * @return the column size
     */
    public int getColumnSize() {
        return columnSize;
    }

    /**
     * Sets the column size.
     *
     * @param columnSize the new column size
     */
    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    /**
     * Gets the c property name.
     *
     * @return the c property name
     */
    public String getCapitalizePropertyName() {
        if (capitalizePropertyName == null) {
            capitalizePropertyName = Utils.capitalize(propertyName);
        }
        return capitalizePropertyName;
    }

    /**
     * Sets the c property name.
     *
     * @param capitalizePropertyName the new c property name
     */
    public void setCapitalizePropertyName(String capitalizePropertyName) {
        capitalizePropertyName = capitalizePropertyName;
    }

    /**
     * Gets the data type.
     *
     * @return the data type
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * Sets the data type.
     *
     * @param dataType the new data type
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;

    }

    /**
     * Gets the data type enum.
     *
     * @return the data type enum
     */
    public DataTypeEnum getDataTypeEnum() {
        return dataTypeEnum;
    }

    /**
     * Sets the data type enum.
     *
     * @param dataTypeEnum the new data type enum
     */
    public void setDataTypeEnum(DataTypeEnum dataTypeEnum) {
        this.dataTypeEnum = dataTypeEnum;
    }

    /**
     * Gets the data type name.
     *
     * @return the data type name
     */
    public String getDataTypeName() {
        return dataTypeName;
    }

    /**
     * Sets the data type name.
     *
     * @param dataTypeName the new data type name
     */
    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
        dataTypeEnum = DataTypeEnum.get(dataTypeName);
        if (dataTypeEnum == null) {
            return;
        }
        setJdbcType(dataTypeEnum.getJdbcType());
        setJavaType(dataTypeEnum.getJavaType());
        setCsType(dataTypeEnum.getCsType());
        setFullJavaType(dataTypeEnum.getFullJavaType());
    }

    /**
     * Gets the decimal digits.
     *
     * @return the decimal digits
     */
    public int getDecimalDigits() {
        return decimalDigits;
    }

    /**
     * Sets the decimal digits.
     *
     * @param decimalDigits the new decimal digits
     */
    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValue the new default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        if (displayName != null) {
            return displayName;
        }
        if (Utils.hasText(remarks)) {
            displayName = remarks;
            Matcher matcher = SchemaConstraints.SYMBOL_PATTERN.matcher(displayName);
            if (matcher.find()) {
                displayName = displayName.substring(0, matcher.start());
            }
        } else {
            displayName = propertyName;
        }

        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName the new display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the easy UI class for edit.
     *
     * @return the easy UI class for edit
     */
    public String getEasyUIClassForEdit() {

        if (!isEditable()) {
            return "easyui-textbox";
        }

        if (isStringColumn()) {
            if (isNotNull()
                    || columnName.toLowerCase().endsWith("email")
                    || columnName.toLowerCase().endsWith("url")) {
                return "easyui-textbox";
            }
        } else if (isInteger()) {
            if (columnName.toLowerCase().endsWith("_time")) {
                return "easyui-datetimebox";
            } else {
                return "easyui-numberbox";
            }
        }
        return "easyui-textbox";
    }

    /**
     * Gets the easy UI class for search.
     *
     * @return the easy UI class for search
     */
    public String getEasyUIClassForSearch() {
        if (isInteger()) {
            if (columnName.toLowerCase().endsWith("_time")) {
                return "easyui-datetimebox";
            }
        }
        return "easyui-textbox";
    }

    /**
     * Gets the easy UI input option for edit.
     *
     * @return the easy UI input option for edit
     */
    public String getEasyUIInputOptionForEdit() {
        if (!isEditable()) {
            return "";
        }

        final StringBuilder sbBuilder = new StringBuilder();
        if (isStringColumn()) {
            if (isNotNull()) {
                sbBuilder.append("required:true,");
            }
            if (propertyName.toLowerCase().endsWith("url")) {
                sbBuilder.append("validType:['url','length[0,").append(columnSize).append("]'],");
            } else if (propertyName.toLowerCase().endsWith("email")) {
                sbBuilder.append("validType:['email','length[0,").append(columnSize).append("]'],");
            } else {
                sbBuilder.append("validType:['length[0,").append(columnSize).append("]'],");
            }
        }
        if (sbBuilder.length() > 0) {
            final String options = sbBuilder.substring(0, sbBuilder.length() - 1);
            return "data-options=\"" + options + "\" ";
        }
        return "";
    }

    /**
     * Gets the full java type.
     *
     * @return the full java type
     */
    public String getFullJavaType() {
        return fullJavaType;
    }

    /**
     * Sets the full java type.
     *
     * @param fullJavaType the new full java type
     */
    public void setFullJavaType(String fullJavaType) {
        this.fullJavaType = fullJavaType;
    }

    /**
     * 获取Getter名称.
     *
     * @return the getter
     */
    public String getGetter() {
        if (getter == null) {
            if (isBooleanColumn()) {
                if (propertyName.startsWith("is")) {
                    getter = propertyName;
                }
            }
            getter = "get" + Utils.capitalize(propertyName);
        }
        return getter;
    }

    /**
     * Sets the getter.
     *
     * @param getterName the new getter
     */
    public void setGetter(String getterName) {
        getter = getterName;
    }

    /**
     * Gets the checks if is autoincrement.
     *
     * @return the checks if is autoincrement
     */
    public String getIsAutoincrement() {
        return isAutoincrement;
    }

    /**
     * Sets the checks if is autoincrement.
     *
     * @param isAutoincrement the new checks if is autoincrement
     */
    public void setIsAutoincrement(String isAutoincrement) {
        this.isAutoincrement = isAutoincrement;
    }

    /**
     * Gets the java type.
     *
     * @return the java type
     */
    public String getJavaType() {
        return javaType;
    }

    /**
     * Sets the java type.
     *
     * @param javaType the new java type
     */
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    /**
     * Gets the jdbc type.
     *
     * @return the jdbc type
     */
    public String getJdbcType() {
        return jdbcType;
    }

    /**
     * Sets the jdbc type.
     *
     * @param jdbcType the new jdbc type
     */
    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    /**
     * Gets the property name.
     *
     * @return the property name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the property name.
     *
     * @param propertyName the new property name
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getCsPropertyName() {
        return csPropertyName;
    }

    /**
     * Gets the property var name.
     *
     * @return the property var name
     */
    public String getPropertyVarName() {
        return propertyVarName;
    }

    /**
     * Sets the property var name.
     *
     * @param propertyVarName the new property var name
     */
    public void setPropertyVarName(String propertyVarName) {
        this.propertyVarName = propertyVarName;
    }

    /**
     * Gets the remarks.
     *
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the remarks.
     *
     * @param remarks the new remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Gets the setter.
     *
     * @return the setter
     */
    public String getSetter() {
        if (setter == null) {
            setter = "set" + Utils.capitalize(propertyName);
        }
        return setter;
    }

    /**
     * Sets the setter.
     *
     * @param setter the new setter
     */
    public void setSetter(String setter) {
        this.setter = setter;
    }

    /**
     * Gets the table schema.
     *
     * @return the table schema
     */
    public TableSchema getTableSchema() {
        return tableSchema;
    }

    /**
     * Sets the table schema.
     *
     * @param tableSchema the new table schema
     */
    public void setTableSchema(TableSchema tableSchema) {
        this.tableSchema = tableSchema;
    }

    /**
     * 获取测试值.
     *
     * @return the test value
     */
    public String getTestValue() {

        if (dataTypeEnum == DataTypeEnum.YEAR) {
            return "\"2017\"";
        }

        if (dataTypeEnum == DataTypeEnum.TIME) {
            return "\"12:12:12\"";
        }

        if (isStringColumn()) {
            return "\"test string\"";
        }

        if (DataTypeEnum.BIGINT.equals(dataTypeEnum)) {
            return propertyName.endsWith("Time") || propertyName.endsWith("Date") ? "System.currentTimeMillis()" : "1L";
        }

        if (isIntColumn()) {
            int now = (int) (System.currentTimeMillis() / 1000);
            return propertyName.endsWith("Time") || propertyName.endsWith("Date") ? "" + now + "" : "1";
        }

        if (isShortColumn()) {
            return "(short) 10";
        }

        if (isByteColumn()) {
            return "(byte)1";
        }
        if (isByteArrayColumn()) {
            return "new byte[]{1,2,3}";
        }
        if (DataTypeEnum.DECIMAL.equals(dataTypeEnum)) {
            return "new BigDecimal(10.01)";
        }

        if (DataTypeEnum.DOUBLE.equals(dataTypeEnum)) {
            return "10.01d";
        }

        if (DataTypeEnum.FLOAT.equals(dataTypeEnum)) {
            return "10.01f";
        }

        if (isDateColumn()) {
            return "new Date()";
        }

        if (DataTypeEnum.BIT.equals(dataTypeEnum)) {
            return "true";
        }
        return "null";
    }

    /**
     * 获取TH的选项.
     *
     * @return the th data options
     */
    public String getThDataOptions() {

        final StringBuilder sbBuilder = new StringBuilder("field:'" + propertyName + "'");
        if (columnName.endsWith("_time")) {
            sbBuilder.append(",formatter:dateFormatter");
        } else if (dataTypeEnum.equals(DataTypeEnum.BIT)) {
            sbBuilder.append(",formatter:yesnoFormatter");
        }
        return sbBuilder.toString();
    }

    /**
     * Checks if is auto increment.
     *
     * @return true, if is auto increment
     */
    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    /**
     * Sets the auto increment.
     *
     * @param isAutoIncrement the new auto increment
     */
    public void setAutoIncrement(boolean isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }

    /**
     * 参照方法名.
     *
     * @return true, if is boolean column
     */
    public boolean isBooleanColumn() {
        return dataTypeEnum.getDataType() == DataTypeEnum.BIT.getDataType();
    }

    /**
     * 是否是创建时间列.
     *
     * @return true, if is creates the time column
     */
    public boolean isCreateTimeColumn() {
        for (final String name : SchemaConstraints.CREATE_TIME_COLUMN) {
            if (name.equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是创建用户列.
     *
     * @return true, if is creates the userid column
     */
    public boolean isCreateUserColumn() {
        for (final String name : SchemaConstraints.CREATE_USER_COLUMN) {
            if (name.equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is date column.
     *
     * @return true, if is date column
     */
    public boolean isDateColumn() {
        if (dataTypeEnum == null) {
            return false;
        }
        if (dataTypeEnum.isDate()) {
            return true;
        }

        return (isIntColumn() || DataTypeEnum.BIGINT.equals(dataTypeEnum) || DataTypeEnum.INTEGER.equals(dataTypeEnum))
                && (columnName.toUpperCase().endsWith("TIME") || columnName.toUpperCase().endsWith("DATE"));

    }

    /**
     * Checks if is deleted column.
     *
     * @return true, if is deleted column
     */
    public boolean isDeletedColumn() {
        if (isDeletedColumn == null) {
            isDeletedColumn = SchemaConstraints.DELETED_COLUMN.contains(this.columnName.toUpperCase());
        }
        return isDeletedColumn != null && isDeletedColumn;
    }

    public boolean isVersionColumn() {
        return SchemaConstraints.VERSION_COLUMN.contains(this.columnName.toUpperCase());
    }

    /**
     * Checks if is editable.
     *
     * @return true, if is editable
     */
    public boolean isEditable() {
        return !(isPrimary()
                || isCreateUserColumn()
                || isUpdateUserColumn()
                || isUpdateTimeColumn()
                || isCreateTimeColumn()
                || isVersionColumn()
                || isStatusColumn()
                || isDeletedColumn());
    }

    /**
     * Checks if is equal where.
     *
     * @return true, if is equal where
     */
    public boolean isEqualWhere() {

        boolean typeCheck = isDateColumn()
                || dataTypeEnum.isByteArray()
                || dataTypeEnum.equals(DataTypeEnum.DOUBLE)
                || dataTypeEnum.equals(DataTypeEnum.FLOAT)
                || dataTypeEnum.equals(DataTypeEnum.DECIMAL)
                || dataTypeEnum == DataTypeEnum.TEXT
                || dataTypeEnum == DataTypeEnum.TINYTEXT
                || (dataTypeEnum.isString() && columnSize > 500);
        return !typeCheck;
    }

    /**
     * Checks if is integer.
     *
     * @return true, if is integer
     */
    public boolean isInteger() {
        return dataTypeEnum == DataTypeEnum.BIGINT
                || dataTypeEnum == DataTypeEnum.INTEGER
                || dataTypeEnum == DataTypeEnum.TINYINT
                || dataTypeEnum == DataTypeEnum.SMALLINT;
    }

    /**
     * Checks if is in where.
     *
     * @return true, if is in where
     */
    public boolean isInWhere() {
        return !(isUpdateTimeColumn()
                || isCreateTimeColumn()
                || isDateColumn()
                || isBooleanColumn()) && (isByteColumn() || DataTypeEnum.BIGINT.equals(dataTypeEnum) || isIntColumn() || isBooleanColumn() || isCharOrVarcharColumn() && columnSize < 51);
    }


    /**
     * Checks if is like where.
     *
     * @return true, if is like where
     */
    public boolean isLikeWhere() {
        return !(isUpdateTimeColumn() || isCreateTimeColumn() || isDateColumn() || isBooleanColumn()) && isCharOrVarcharColumn() && columnSize < 100;
    }

    /**
     * Checks if is need validate.
     *
     * @return true, if is need validate
     */
    public boolean isNeedValidate() {
        return isInteger() || isNotNull() || isNumber();
    }

    /**
     * Checks if is not null.
     *
     * @return true, if is not null
     */
    public boolean isNotNull() {
        return !nullable;
    }

    /**
     * Checks if is nullable.
     *
     * @return true, if is nullable
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Sets the nullable.
     *
     * @param nullable the new nullable
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * Checks if is number.
     *
     * @return true, if is number
     */
    public boolean isNumber() {
        return dataTypeEnum == DataTypeEnum.FLOAT
                || dataTypeEnum == DataTypeEnum.DECIMAL
                || dataTypeEnum == DataTypeEnum.INT
                || dataTypeEnum == DataTypeEnum.BIGINT
                || dataTypeEnum == DataTypeEnum.DOUBLE
                || dataTypeEnum == DataTypeEnum.SMALLINT
                || dataTypeEnum == DataTypeEnum.TINYINT;

    }

    /**
     * Checks if is primary.
     *
     * @return true, if is primary
     */
    public boolean isPrimary() {
        return isPrimary;
    }

    /**
     * Sets the primary.
     *
     * @param isPrimary the new primary
     */
    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    /**
     * Checks if is range where.
     *
     * @return true, if is range where
     */
    public boolean isRangeWhere() {
        return isDateColumn() || DataTypeEnum.DECIMAL.equals(dataTypeEnum);
    }

    /**
     * Checks if is status column.
     *
     * @return true, if is status column
     */
    public boolean isStatusColumn() {
        if (isStatusColumn == null) {
            for (String statusName : SchemaConstraints.STATUS_COLUMN) {
                isStatusColumn = columnName.equalsIgnoreCase(statusName);
                if (isStatusColumn) {
                    break;
                }
            }
        }
        return isStatusColumn != null && isStatusColumn;
    }

    /**
     * Checks if is string column.
     *
     * @return true, if is string column
     */
    public boolean isStringColumn() {
        return dataTypeEnum.isString();
    }

    /**
     * Checks if is update time column.
     *
     * @return true, if is update time column
     */
    public boolean isUpdateTimeColumn() {
        for (final String name : SchemaConstraints.UPDATE_TIME_COLUMN) {
            if (name.equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is update userid column.
     *
     * @return true, if is update userid column
     */
    public boolean isUpdateUserColumn() {
        return SchemaConstraints.UPDATE_USER_COLUMN.contains(columnName.toUpperCase());
    }

    /**
     * 参照方法名.
     *
     * @return true, if is byte array column
     */
    public boolean isByteArrayColumn() {
        return dataTypeEnum.isByteArray();
    }

    /**
     * Checks if is byte column.
     *
     * @return true, if is byte column
     */
    public boolean isByteColumn() {
        return DataTypeEnum.TINYINT.equals(dataTypeEnum);
    }

    /**
     * Checks if is char or varchar column.
     *
     * @return true, if is char or varchar column
     */
    public boolean isCharOrVarcharColumn() {
        return DataTypeEnum.VARCHAR.equals(dataTypeEnum)
                || DataTypeEnum.CHAR.equals(dataTypeEnum)
                || DataTypeEnum.NVARCHAR.equals(dataTypeEnum)
                || DataTypeEnum.NCHAR.equals(dataTypeEnum);
    }

    /**
     * Checks if is int column.
     *
     * @return true, if is int column
     */
    public boolean isIntColumn() {

        return DataTypeEnum.INTEGER.equals(dataTypeEnum)
                || DataTypeEnum.MEDIUMINT.equals(dataTypeEnum);
    }

    /**
     * 参照方法名.
     *
     * @return true, if is short column
     */
    public boolean isShortColumn() {
        return DataTypeEnum.SMALLINT.equals(dataTypeEnum);
    }


    public String getDataProvider() {
        if (isDateColumn()) {
            return "data-provider=\"datepicker\"";
        }
        return "";
    }

    public String getDataRender() {
        if (isDateColumn() && (isIntColumn() || isLongColumn())) {
            return "data-render=\"dateRender\"";
        }
        return "";
    }

    public boolean isLongColumn() {
        return DataTypeEnum.BIGINT.equals(this.dataTypeEnum);
    }

    /**
     * 参照方法名.
     *
     * @return
     */
    public boolean isUrlColumn() {

        return isStringColumn()
                && (this.columnName.toLowerCase().endsWith("url")
                || this.columnName.toLowerCase().endsWith("website"));
    }

    /**
     * 参照方法名.
     *
     * @return
     */
    public boolean isEmailColumn() {
        return isStringColumn() && (this.columnName.toLowerCase().endsWith("email"));
    }

    public boolean isRemarkColumn() {
        return isStringColumn() && (
                this.columnName.equalsIgnoreCase("remark")
        );
    }

    public String getEnglishName() {
        String[] split = this.columnName.split("-");
        if (split.length == 1) {
            return split[0];
        }
        StringBuilder sb = new StringBuilder(split[0]);
        for (int i = 1; i < split.length; i++) {
            sb.append(" ").append(split[i]);
        }
        return sb.toString();
    }

    public boolean isMobileColumn() {
        return isStringColumn() && this.getColumnName().toUpperCase().endsWith("MOBILE");
    }


    public String getCsType() {
        return csType;
    }

    public void setCsType(String csType) {
        this.csType = csType;
    }

    public String getNullCsType() {
        if (isStringColumn() || isByteArrayColumn()) {
            return csType;
        }
        return csType + "?";
    }

    public boolean generateCsProperty() {
        boolean notGenerate =
                propertyName.equalsIgnoreCase("id")
                        || isCreateTimeColumn()
                        || isCreateUserColumn();
        return !notGenerate;
    }

    public boolean getMobileColumn() {
        return isStringColumn() && (this.columnName.toLowerCase().endsWith("mobile")
        );
    }

    public boolean isCreateRequestColumn() {
        boolean notCreateRequestColumn =
                this.isAutoIncrement
                        || this.isPrimary
                        || this.isStatusColumn
                        || this.isCreateTimeColumn()
                        || this.isUpdateTimeColumn()
                        || this.isUpdateUserColumn()
                        || this.isDeletedColumn();
        return !notCreateRequestColumn;

    }

    public boolean isUpdateRequestColumn() {
        boolean notUpdateRequestColumn =
                this.isCreateTimeColumn()
                        || this.isPrimary
                        || this.isAutoIncrement
                        || this.isStatusColumn()
                        || this.isCreateUserColumn()
                        || this.isUpdateTimeColumn()
                        || this.isDeletedColumn();
        return !notUpdateRequestColumn;

    }
}
