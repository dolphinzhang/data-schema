package org.dol.database.schema;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.dol.database.utils.Utils;

import java.util.regex.Matcher;


@Getter
@Setter
public class ColumnSchema {

    private TableSchema tableSchema;
    private String columnName;
    private int columnSize;
    private int decimalDigits;
    private boolean nullable;
    private String remarks;
    private String defaultValue;
    private boolean isPrimary;
    private boolean isAutoIncrement;
    private String propertyName;
    private String displayName;
    private int dataType;
    private Boolean unsigned = false;
    private String dataTypeName;
    private String jdbcType;
    private String javaType;
    private String csType;
    private String fullJavaType;
    private String propertyVarName;
    private String capitalizePropertyName;
    private DataTypeEnum dataTypeEnum;
    private String getter;
    private String setter;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Boolean isStatusColumn;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Boolean isDeletedColumn;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Boolean cachedCreateTime;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Boolean cachedCreateUser;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Boolean cachedUpdateTime;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Boolean cachedUpdateUser;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Boolean cachedDeleteTime;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Boolean cachedDeleteUser;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Boolean cachedVersion;
    private String csPropertyName;
    private String characterSet;
    private String collation;

    public Double getColumnLength() {
        if (decimalDigits > 0) {
            return Double.parseDouble(columnSize + "." + decimalDigits);
        }
        return (double) columnSize;
    }

    /**
     * 设置列名并自动计算 propertyName、csPropertyName 等派生字段.
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
        final String[] columnNameParts = columnName.split("_");
        final StringBuilder sb = new StringBuilder();
        for (final String namePart : columnNameParts) {
            sb.append(Utils.capitalize(namePart));
        }
        final String propertyFieldName = Utils.uncapitalize(sb.toString());
        this.propertyName = propertyFieldName;
        this.propertyVarName = propertyFieldName;
        this.csPropertyName = sb.toString();
    }

    public String getFieldName() {
        if (columnName.contains("_")) {
            return columnName.toUpperCase();
        } else {
            return (tableSchema.nameWithoutPrefix() + "_" + columnName).toUpperCase();
        }
    }

    public boolean isIdColumn() {
        return this.tableSchema.getIdColumn() == this;
    }

    public String getCapitalizePropertyName() {
        if (capitalizePropertyName == null) {
            capitalizePropertyName = Utils.capitalize(propertyName);
        }
        return capitalizePropertyName;
    }

    /**
     * 设置数据类型名称并自动解析对应的 Java/C#/JDBC 类型.
     */
    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
        dataTypeEnum = DataTypeEnum.get(dataTypeName);
        if (dataTypeEnum == null) {
            return;
        }
        this.jdbcType = dataTypeEnum.getJdbcType();
        this.javaType = dataTypeEnum.getJavaType();
        this.csType = dataTypeEnum.getCsType();
        this.fullJavaType = dataTypeEnum.getFullJavaType();
    }

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

    /** @deprecated Use {@link org.dol.database.utils.ColumnUIHelper#getEasyUIClassForEdit(ColumnSchema)} */
    public String getEasyUIClassForEdit() {
        return org.dol.database.utils.ColumnUIHelper.getEasyUIClassForEdit(this);
    }

    /** @deprecated Use {@link org.dol.database.utils.ColumnUIHelper#getEasyUIClassForSearch(ColumnSchema)} */
    public String getEasyUIClassForSearch() {
        return org.dol.database.utils.ColumnUIHelper.getEasyUIClassForSearch(this);
    }

    /** @deprecated Use {@link org.dol.database.utils.ColumnUIHelper#getEasyUIInputOptionForEdit(ColumnSchema)} */
    public String getEasyUIInputOptionForEdit() {
        return org.dol.database.utils.ColumnUIHelper.getEasyUIInputOptionForEdit(this);
    }

    /**
     * 获取 Getter 名称. 布尔类型以 is 开头的属性返回原名, 否则返回 getXxx.
     */
    public String getGetter() {
        if (getter == null) {
            if (isBooleanColumn() && propertyName.startsWith("is")) {
                getter = propertyName;
            } else {
                getter = "get" + Utils.capitalize(propertyName);
            }
        }
        return getter;
    }

    public String getSetter() {
        if (setter == null) {
            setter = "set" + Utils.capitalize(propertyName);
        }
        return setter;
    }

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
        if (dataTypeEnum == DataTypeEnum.BIGINT) {
            return propertyName.endsWith("Time") || propertyName.endsWith("Date") ? "System.currentTimeMillis()" : "1L";
        }
        if (isIntColumn()) {
            int now = (int) (System.currentTimeMillis() / 1000);
            return propertyName.endsWith("Time") || propertyName.endsWith("Date") ? "" + now : "1";
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
        if (dataTypeEnum == DataTypeEnum.DECIMAL) {
            return "new BigDecimal(10.01)";
        }
        if (dataTypeEnum == DataTypeEnum.DOUBLE) {
            return "10.01d";
        }
        if (dataTypeEnum == DataTypeEnum.FLOAT) {
            return "10.01f";
        }
        if (isDateColumn()) {
            return "new Date()";
        }
        if (dataTypeEnum == DataTypeEnum.BIT) {
            return "true";
        }
        return "null";
    }

    /** @deprecated Use {@link org.dol.database.utils.ColumnUIHelper#getThDataOptions(ColumnSchema)} */
    public String getThDataOptions() {
        return org.dol.database.utils.ColumnUIHelper.getThDataOptions(this);
    }

    // ========== 类型判断方法 ==========

    public boolean isBooleanColumn() {
        return dataTypeEnum != null && dataTypeEnum.getDataType() == DataTypeEnum.BIT.getDataType();
    }

    public boolean isCreateTimeColumn() {
        if (cachedCreateTime == null) {
            cachedCreateTime = SchemaConstraints.CREATE_TIME_COLUMN.contains(columnName.toUpperCase());
        }
        return cachedCreateTime;
    }

    public boolean isCreateUserColumn() {
        if (cachedCreateUser == null) {
            cachedCreateUser = SchemaConstraints.CREATE_USER_COLUMN.contains(columnName.toUpperCase());
        }
        return cachedCreateUser;
    }

    public boolean isDateColumn() {
        if (dataTypeEnum == null) {
            return false;
        }
        if (dataTypeEnum.isDate()) {
            return true;
        }
        return (isIntColumn() || dataTypeEnum == DataTypeEnum.BIGINT)
                && (columnName.toUpperCase().endsWith("TIME") || columnName.toUpperCase().endsWith("DATE"));
    }

    public boolean isDeletedColumn() {
        if (isDeletedColumn == null) {
            isDeletedColumn = SchemaConstraints.DELETED_COLUMN.contains(this.columnName.toUpperCase());
        }
        return isDeletedColumn;
    }

    public boolean isVersionColumn() {
        if (cachedVersion == null) {
            cachedVersion = SchemaConstraints.VERSION_COLUMN.contains(this.columnName.toUpperCase());
        }
        return cachedVersion;
    }

    public boolean notEditable() {
        return isPrimary()
                || isCreateUserColumn()
                || isUpdateUserColumn()
                || isUpdateTimeColumn()
                || isCreateTimeColumn()
                || isVersionColumn()
                || isStatusColumn()
                || isDeletedColumn();
    }

    public boolean isEqualWhere() {
        if (dataTypeEnum == null) {
            return false;
        }
        boolean typeCheck = isDateColumn()
                || dataTypeEnum.isByteArray()
                || dataTypeEnum.isSpatial()
                || dataTypeEnum.isJson()
                || dataTypeEnum == DataTypeEnum.DOUBLE
                || dataTypeEnum == DataTypeEnum.FLOAT
                || dataTypeEnum == DataTypeEnum.DECIMAL
                || dataTypeEnum == DataTypeEnum.TEXT
                || dataTypeEnum == DataTypeEnum.TINYTEXT
                || (dataTypeEnum.isString() && columnSize > 500);
        return !typeCheck;
    }

    public boolean isInteger() {
        return dataTypeEnum == DataTypeEnum.BIGINT
                || dataTypeEnum == DataTypeEnum.INT
                || dataTypeEnum == DataTypeEnum.INTEGER
                || dataTypeEnum == DataTypeEnum.MEDIUMINT
                || dataTypeEnum == DataTypeEnum.TINYINT
                || dataTypeEnum == DataTypeEnum.SMALLINT;
    }

    public boolean isInWhere() {
        return !(isUpdateTimeColumn()
                || isCreateTimeColumn()
                || isDateColumn()
                || isBooleanColumn())
                && (isByteColumn() || dataTypeEnum == DataTypeEnum.BIGINT || isIntColumn()
                || isCharOrVarcharColumn() && columnSize < 51);
    }

    public boolean isLikeWhere() {
        return isCharOrVarcharColumn()
                && columnSize < 129
                && !(isUpdateTimeColumn()
                || isCreateTimeColumn()
                || isDateColumn()
                || isBooleanColumn());
    }

    public boolean isKeywordColumn() {
        return isCharOrVarcharColumn()
                && columnSize < 129
                && !(isUpdateTimeColumn()
                || isCreateTimeColumn()
                || isDateColumn()
                || isBooleanColumn()
                || isUpdateUserColumn()
                || isCreateUserColumn()
                || isDeleteUserColumn());
    }

    public boolean isNeedValidate() {
        return isInteger() || isNotNull() || isNumber();
    }

    public boolean isNotNull() {
        return !nullable;
    }

    public boolean isNumber() {
        return dataTypeEnum == DataTypeEnum.FLOAT
                || dataTypeEnum == DataTypeEnum.DECIMAL
                || dataTypeEnum == DataTypeEnum.INT
                || dataTypeEnum == DataTypeEnum.INTEGER
                || dataTypeEnum == DataTypeEnum.MEDIUMINT
                || dataTypeEnum == DataTypeEnum.BIGINT
                || dataTypeEnum == DataTypeEnum.DOUBLE
                || dataTypeEnum == DataTypeEnum.SMALLINT
                || dataTypeEnum == DataTypeEnum.TINYINT;
    }

    public boolean isRangeWhere() {
        if (isDateColumn() || dataTypeEnum == DataTypeEnum.DECIMAL) {
            return true;
        }
        if (dataTypeEnum == DataTypeEnum.INTEGER || dataTypeEnum == DataTypeEnum.INT || dataTypeEnum == DataTypeEnum.MEDIUMINT) {
            return this.columnName.equalsIgnoreCase("to")
                    || this.columnName.equalsIgnoreCase("age")
                    || this.columnName.equalsIgnoreCase("birthday")
                    || this.columnName.equalsIgnoreCase("start")
                    || this.columnName.equalsIgnoreCase("end")
                    || this.columnName.equalsIgnoreCase("width")
                    || this.columnName.equalsIgnoreCase("height")
                    || this.columnName.equalsIgnoreCase("length")
                    || this.columnName.equalsIgnoreCase("size")
                    || this.columnName.startsWith("start_")
                    || this.columnName.startsWith("end_")
                    || this.columnName.startsWith("from_")
                    || this.columnName.startsWith("to_")
                    || this.columnName.endsWith("_width")
                    || this.columnName.endsWith("_height")
                    || this.columnName.endsWith("_length")
                    || this.columnName.endsWith("_size")
                    || this.columnName.endsWith("_age")
                    || this.columnName.endsWith("_birthday");
        }
        return false;
    }

    public boolean isStatusColumn() {
        if (isStatusColumn == null) {
            isStatusColumn = SchemaConstraints.STATUS_COLUMN.contains(columnName.toUpperCase());
        }
        return isStatusColumn;
    }

    public boolean isDeleteTimeColumn() {
        if (cachedDeleteTime == null) {
            cachedDeleteTime = SchemaConstraints.DELETE_TIME_COLUMN.contains(columnName.toUpperCase());
        }
        return cachedDeleteTime;
    }

    public boolean isDeleteUserColumn() {
        if (cachedDeleteUser == null) {
            cachedDeleteUser = SchemaConstraints.DELETE_USER_COLUMN.contains(columnName.toUpperCase());
        }
        return cachedDeleteUser;
    }

    public boolean isCompanyColumn() {
        return columnName.equalsIgnoreCase(SchemaConstraints.COMPANY_ID);
    }

    public boolean isStringColumn() {
        return dataTypeEnum != null && dataTypeEnum.isString();
    }

    public boolean isUpdateTimeColumn() {
        if (cachedUpdateTime == null) {
            cachedUpdateTime = SchemaConstraints.UPDATE_TIME_COLUMN.contains(columnName.toUpperCase());
        }
        return cachedUpdateTime;
    }

    public boolean isUpdateUserColumn() {
        if (cachedUpdateUser == null) {
            cachedUpdateUser = SchemaConstraints.UPDATE_USER_COLUMN.contains(columnName.toUpperCase());
        }
        return cachedUpdateUser;
    }

    public boolean isByteArrayColumn() {
        return dataTypeEnum != null && dataTypeEnum.isByteArray();
    }

    public boolean isByteColumn() {
        return dataTypeEnum == DataTypeEnum.TINYINT;
    }

    public boolean isCharOrVarcharColumn() {
        return dataTypeEnum == DataTypeEnum.VARCHAR
                || dataTypeEnum == DataTypeEnum.CHAR
                || dataTypeEnum == DataTypeEnum.NVARCHAR
                || dataTypeEnum == DataTypeEnum.NCHAR;
    }

    public boolean isIntColumn() {
        return dataTypeEnum == DataTypeEnum.INT
                || dataTypeEnum == DataTypeEnum.INTEGER
                || dataTypeEnum == DataTypeEnum.MEDIUMINT;
    }

    public boolean isShortColumn() {
        return dataTypeEnum == DataTypeEnum.SMALLINT;
    }

    public boolean isLongColumn() {
        return dataTypeEnum == DataTypeEnum.BIGINT;
    }

    /** @deprecated Use {@link org.dol.database.utils.ColumnUIHelper#getDataProvider(ColumnSchema)} */
    public String getDataProvider() {
        return org.dol.database.utils.ColumnUIHelper.getDataProvider(this);
    }

    /** @deprecated Use {@link org.dol.database.utils.ColumnUIHelper#getDataRender(ColumnSchema)} */
    public String getDataRender() {
        return org.dol.database.utils.ColumnUIHelper.getDataRender(this);
    }

    public boolean isUrlColumn() {
        return isStringColumn()
                && (this.columnName.toLowerCase().endsWith("url")
                || this.columnName.toLowerCase().endsWith("website"));
    }

    public boolean isEmailColumn() {
        return isStringColumn() && this.columnName.toLowerCase().endsWith("email");
    }

    public boolean isRemarkColumn() {
        return isStringColumn() && this.columnName.equalsIgnoreCase("remark");
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
        return isMobileColumn();
    }

    public boolean isCreateRequestColumn() {
        boolean notCreateRequestColumn =
                this.isAutoIncrement
                        || this.isPrimary
                        || this.isStatusColumn()
                        || this.isCreateTimeColumn()
                        || this.isUpdateTimeColumn()
                        || this.isCreateUserColumn()
                        || this.isUpdateUserColumn()
                        || this.isCompanyColumn()
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
                        || this.isUpdateUserColumn()
                        || this.isCompanyColumn()
                        || this.isDeletedColumn();
        return !notUpdateRequestColumn;
    }

    public boolean isDeleteColumn() {
        return SchemaConstraints.DELETE_COLUMN.contains(this.columnName.toUpperCase());
    }
}
