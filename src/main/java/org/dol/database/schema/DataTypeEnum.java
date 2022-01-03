package org.dol.database.schema;

import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc

/**
 * 数据类型枚举.
 *
 * @author dolphin
 * @date 2017年2月20日 下午4:10:51
 */
public enum DataTypeEnum {

    /**
     * The bigint.
     */
    BIGINT("BIGINT", -5, "BIGINT", "Long", "long", "java.lang.Long"),

    /**
     * The int.
     */
    INT("INT", 4, "INTEGER", "Integer", "int", "java.lang.Integer"),

    /**
     * The integer.
     */
    INTEGER("INTEGER", 4, "INTEGER", "Integer", "int", "java.lang.Integer"),

    /**
     * The tinyint.
     */
    TINYINT("TINYINT", -6, "TINYINT", "Byte", "byte", "java.lang.Byte"),

    /**
     * The smallint.
     */
    SMALLINT("SMALLINT", 5, "SMALLINT", "Short", "short", "java.lang.Short"),

    /**
     * The mediumint.
     */
    MEDIUMINT("MEDIUMINT", 4, "INTEGER", "Integer", "int", "java.lang.Integer"),

    /**
     * The char.
     */
    CHAR("CHAR", 1, "CHAR", "String", "string", "java.lang.String"),

    /**
     * The varchar.
     */
    VARCHAR("VARCHAR", 12, "VARCHAR", "String", "string", "java.lang.String"),

    NVARCHAR("NVARCHAR", -9, "NVARCHAR", "String", "string", "java.lang.String"),


    NCHAR("NCHAR", -15, "NCHAR", "String", "string", "java.lang.String"),

    /**
     * The tinytext.
     */
    TINYTEXT("TINYTEXT", 12, "LONGVARCHAR", "String", "string", "java.lang.String"),

    /**
     * The text.
     */
    TEXT("TEXT", -1, "LONGVARCHAR", "String", "string", "java.lang.String"),
    /**
     *
     */
    REAL("REAL", -1, "REAL", "Float", "float", "java.lang.Float"),

    /**
     * The mediumtext.
     */
    MEDIUMTEXT("MEDIUMTEXT", -1, "LONGVARCHAR", "String", "string", "java.lang.String"),

    NTEXT("NTEXT", -1, "NTEXT", "String", "string", "java.lang.String"),

    /**
     * The longtext.
     */
    LONGTEXT("LONGTEXT", -1, "LONGVARCHAR", "String", "string", "java.lang.String"),

    /**
     * The float.
     */
    FLOAT("FLOAT", 7, "FLOAT", "Float", "float", "java.lang.Float"),

    /**
     * The double.
     */
    DOUBLE("DOUBLE", 8, "DOUBLE", "Double", "double", "java.lang.Double"),

    /**
     * The decimal.
     */
    DECIMAL("DECIMAL", 3, "DECIMAL", "BigDecimal", "double", "java.math.BigDecimal"),

    /**
     * The date.
     */
    DATE("DATE", 91, "DATE", "Date", "DateTime","java.util.Date"),

    /**
     * The datetime.
     */
    DATETIME("DATETIME", 93, "TIMESTAMP", "Date","DateTime", "java.util.Date"),

    /**
     * The time.
     */
    TIME("TIME", 92, "VARCHAR", "String", "string","java.lang.String"),

    /**
     * The year.
     */
    YEAR("YEAR", 91, "VARCHAR", "String", "string","java.lang.String"),

    /**
     * The timestamp.
     */
    TIMESTAMP("TIMESTAMP", 93, "TIMESTAMP", "Date","DateTime", "java.util.Date"),

    /**
     * The tinyblob.
     */
    TINYBLOB("TINYBLOB", -2, "BLOB", "byte[]", "byte[]", "java.lang.Byte"),

    /**
     * The blob.
     */
    BLOB("BLOB", -4, "BLOB", "byte[]", "byte[]","java.lang.Byte"),

    /**
     * The mediumblob.
     */
    MEDIUMBLOB("MEDIUMBLOB", -4, "BLOB", "byte[]", "byte[]", "java.lang.Byte"),

    /**
     * The longblob.
     */
    LONGBLOB("LONGBLOB", -4, "BLOB", "byte[]","byte[]", "java.lang.Byte"),

    /**
     * The binary.
     */
    BINARY("BINARY", -2, "BINARY", "byte[]", "byte[]","java.lang.Byte"),

    /**
     * The varbinary.
     */
    VARBINARY("VARBINARY", -3, "VARBINARY", "byte[]","byte[]", "java.lang.Byte"),

    BOOLEAN("BOOLEAN", 4, "BOOLEAN", "Boolean", "bool","java.lang.Boolean"),

    /**
     * The bit.
     */
    BIT("BIT", -7, "BIT", "Boolean", "bool","java.lang.Boolean"),;
    // TEXT(0, "VARCHAR", "String", "java.lang.String"),

    /**
     * The Constant KEYED_DATA_TYPE_ENUM.
     */
    private static final Map<String, DataTypeEnum> KEYED_DATA_TYPE_ENUM = new HashMap<>();

    static {
        final DataTypeEnum[] values = DataTypeEnum.values();
        for (final DataTypeEnum dataTypeEnum : values) {
            KEYED_DATA_TYPE_ENUM.put(dataTypeEnum.dataTypeName, dataTypeEnum);
        }
    }

    /**
     * The data type name.
     */
    private String dataTypeName;
    /**
     * The data type.
     */
    private int dataType;
    /**
     * The jdbc type.
     */
    private String jdbcType;
    /**
     * The java type.
     */
    private String javaType;

    private String csType;

    /**
     * The full java type.
     */
    private String fullJavaType;

    /**
     * Instantiates a new data type enum.
     *
     * @param dataTypeName the data type name
     * @param dataType     the data type
     * @param jdbcType     the jdbc type
     * @param javaType     the java type
     * @param fullJavaType the full java type
     */
    DataTypeEnum(String dataTypeName, int dataType, String jdbcType, String javaType, String csType, String fullJavaType) {
        setDataTypeName(dataTypeName);
        setDataType(dataType);
        setJdbcType(jdbcType);
        this.javaType = javaType;
        this.csType = csType;
        setFullJavaType(fullJavaType);
    }

    /**
     * 获取数据类型枚举.
     *
     * @param dataTypeName the data type name
     * @return the data type enum
     */
    public static DataTypeEnum get(String dataTypeName) {
        return KEYED_DATA_TYPE_ENUM.get(dataTypeName.toUpperCase());
    }

    /**
     * Equals.
     *
     * @param other the other
     * @return true, if successful
     */
    public boolean equals(DataTypeEnum other) {
        return other != null && dataType == other.dataType;

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
     * Gets the java type.
     *
     * @return the java type
     */
    public String getJavaType() {
        return javaType;
    }

    public String getCsType() {
        return csType;
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
     * 参照方法名.
     *
     * @return true, if is byte array
     */
    public boolean isByteArray() {
        return javaType.equalsIgnoreCase(BINARY.getJavaType());
    }

    /**
     * 参照方法名.
     *
     * @return true, if is date
     */
    public boolean isDate() {
        return javaType.equalsIgnoreCase(DATETIME.javaType);
    }

    /**
     * 参照方法名.
     *
     * @return true, if is string
     */
    public boolean isString() {
        return javaType.equalsIgnoreCase(VARCHAR.javaType);
    }

}
