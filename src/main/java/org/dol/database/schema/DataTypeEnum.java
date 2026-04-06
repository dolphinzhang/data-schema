package org.dol.database.schema;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
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
    DATE("DATE", 91, "DATE", "Date", "DateTime", "java.util.Date"),

    /**
     * The datetime.
     */
    DATETIME("DATETIME", 93, "TIMESTAMP", "Date", "DateTime", "java.util.Date"),

    /**
     * The time.
     */
    TIME("TIME", 92, "VARCHAR", "String", "string", "java.lang.String"),

    /**
     * The year.
     */
    YEAR("YEAR", 91, "VARCHAR", "String", "string", "java.lang.String"),

    /**
     * The timestamp.
     */
    TIMESTAMP("TIMESTAMP", 93, "TIMESTAMP", "Date", "DateTime", "java.util.Date"),

    /**
     * The tinyblob.
     */
    TINYBLOB("TINYBLOB", -2, "BLOB", "byte[]", "byte[]", "java.lang.Byte"),

    /**
     * The blob.
     */
    BLOB("BLOB", -4, "BLOB", "byte[]", "byte[]", "java.lang.Byte"),

    /**
     * The mediumblob.
     */
    MEDIUMBLOB("MEDIUMBLOB", -4, "BLOB", "byte[]", "byte[]", "java.lang.Byte"),

    /**
     * The longblob.
     */
    LONGBLOB("LONGBLOB", -4, "BLOB", "byte[]", "byte[]", "java.lang.Byte"),

    /**
     * The binary.
     */
    BINARY("BINARY", -2, "BINARY", "byte[]", "byte[]", "java.lang.Byte"),

    /**
     * The varbinary.
     */
    VARBINARY("VARBINARY", -3, "VARBINARY", "byte[]", "byte[]", "java.lang.Byte"),

    BOOLEAN("BOOLEAN", 4, "BOOLEAN", "Boolean", "bool", "java.lang.Boolean"),

    /**
     * The bit.
     */
    BIT("BIT", -7, "BIT", "Boolean", "bool", "java.lang.Boolean"),

    // DECIMAL alias
    NUMERIC("NUMERIC", 3, "DECIMAL", "BigDecimal", "double", "java.math.BigDecimal"),

    // String-like types
    ENUM("ENUM", 1, "CHAR", "String", "string", "java.lang.String"),
    SET("SET", 1, "CHAR", "String", "string", "java.lang.String"),
    JSON("JSON", -1, "LONGVARCHAR", "String", "string", "java.lang.String"),

    // Spatial types
    GEOMETRY("GEOMETRY", -2, "BINARY", "byte[]", "byte[]", "java.lang.Byte"),
    POINT("POINT", -2, "BINARY", "byte[]", "byte[]", "java.lang.Byte"),
    LINESTRING("LINESTRING", -2, "BINARY", "byte[]", "byte[]", "java.lang.Byte"),
    POLYGON("POLYGON", -2, "BINARY", "byte[]", "byte[]", "java.lang.Byte"),
    MULTIPOINT("MULTIPOINT", -2, "BINARY", "byte[]", "byte[]", "java.lang.Byte"),
    MULTILINESTRING("MULTILINESTRING", -2, "BINARY", "byte[]", "byte[]", "java.lang.Byte"),
    MULTIPOLYGON("MULTIPOLYGON", -2, "BINARY", "byte[]", "byte[]", "java.lang.Byte"),
    GEOMETRYCOLLECTION("GEOMETRYCOLLECTION", -2, "BINARY", "byte[]", "byte[]", "java.lang.Byte"),
    ;
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

    private final String dataTypeName;
    private final int    dataType;
    private final String jdbcType;
    private final String javaType;
    private final String csType;
    private final String fullJavaType;

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
        this.dataTypeName = dataTypeName;
        this.dataType = dataType;
        this.jdbcType = jdbcType;
        this.javaType = javaType;
        this.csType = csType;
        this.fullJavaType = fullJavaType;
    }

    public static DataTypeEnum get(String dataTypeName) {
        return KEYED_DATA_TYPE_ENUM.get(dataTypeName.toUpperCase());
    }

    /**
     * 判断是否与另一个 DataTypeEnum 具有相同的 JDBC dataType 编码.
     */
    public boolean sameDataType(DataTypeEnum other) {
        return other != null && dataType == other.dataType;
    }

    /**
     * 参照方法名.
     *
     * @return true, if is byte array
     */
    public boolean isByteArray() {
        return javaType.equalsIgnoreCase(BINARY.getJavaType());
    }

    public boolean isDate() {
        return javaType.equalsIgnoreCase(DATETIME.javaType);
    }

    /**
     * 是否是字符串家族类型 (javaType 为 String).
     * 注意: JSON、ENUM、SET、TIME、YEAR 也属于此家族.
     */
    public boolean isString() {
        return javaType.equalsIgnoreCase(VARCHAR.javaType);
    }

    /** 是否是 JSON 类型. */
    public boolean isJson() {
        return this == JSON;
    }

    /** 是否是空间类型 (GEOMETRY, POINT, LINESTRING, POLYGON 等). */
    public boolean isSpatial() {
        return this == GEOMETRY || this == POINT || this == LINESTRING || this == POLYGON
                || this == MULTIPOINT || this == MULTILINESTRING || this == MULTIPOLYGON
                || this == GEOMETRYCOLLECTION;
    }

    /** 是否是 ENUM 或 SET 类型. */
    public boolean isEnumOrSet() {
        return this == ENUM || this == SET;
    }

    /** 是否是整数家族 (INT, INTEGER, BIGINT, SMALLINT, TINYINT, MEDIUMINT). */
    public boolean isIntFamily() {
        return this == INT || this == INTEGER || this == MEDIUMINT
                || this == BIGINT || this == SMALLINT || this == TINYINT;
    }

    /** 是否是数值家族 (整数 + 浮点 + 定点). */
    public boolean isNumeric() {
        return isIntFamily()
                || this == FLOAT || this == DOUBLE || this == REAL
                || this == DECIMAL || this == NUMERIC;
    }

}
