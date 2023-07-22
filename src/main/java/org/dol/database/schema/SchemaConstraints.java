package org.dol.database.schema;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc

/**
 * The Class SchemaConstraints.
 */
public abstract class SchemaConstraints {

    /**
     * The Constant UPDATE_USER_COLUMN.
     */
    public static final List<String> UPDATE_USER_COLUMN = Arrays.asList(
            "UPDATE_USERID",
            "UPDATE_USER_ID",
            "UPDATE_USER",
            "EDIT_USER",
            "EDITUSER",
            "MODIFY_USER",
            "UPDATEUSERID",
            "UPDATEUSER",
            "MODIFYUSER",
            "UPT_USER",
            "UPT_USER_ID",
            "UPTUSER"
    );

    /**
     * The Constant UPDATE_TIME_COLUMN.
     */
    public static final List<String> UPDATE_TIME_COLUMN = Arrays.asList("UPDATE_TIME", "UPDATE_DATE", "UPDATETIME", "UPDATEDATE", "LAST_UPDATE_TIME", "LAST_UPDATE_DATE");

    /**
     * The Constant CREATE_USER_COLUMN.
     */
    public static final List<String> CREATE_USER_COLUMN = Arrays.asList("CREATE_USER_ID", "CREATE_USERID", "CREATE_USER", "CREATEUSERID", "CREATEUSER");

    /**
     * The Constant CREATE_TIME_COLUMN.
     */
    public static final List<String> CREATE_TIME_COLUMN = Arrays.asList("CREATE_TIME", "CREATE_DATE", "createTime", "createDate");

    /**
     * The Constant DELETED_COLUMN.
     */
    public static final List<String> DELETED_COLUMN = Arrays.asList("IS_DELETED", "DELETE_FLAG", "DELETED", "DELETE_TIME", "DELETE_USER", "DELETED_TIME", "DELETED_USER");

    public static final List<String> DELETE_COLUMN = Arrays.asList("IS_DELETED", "DELETE_FLAG", "DELETED");


    public static final List<String> VERSION_COLUMN = Arrays.asList("VERSION", "V", "VER");
    /**
     * The Constant STATUS_COLUMN.
     */
    public static final List<String> STATUS_COLUMN  = Arrays.asList("STATUS", "STAT");
    public static final List<String> DELETE_USER_COLUMN  = Arrays.asList("DELETE_USER", "DELETED_USER", "DELETED_BY", "DELETE_BY");
    public static final List<String> DELETE_TIME_COLUMN  = Arrays.asList("DELETE_TIME", "DELETED_TIME");
    public static final Pattern SYMBOL_PATTERN = Pattern.compile("[,，.。:：;；\\s\\-—]");

    public static final String COMPANY_ID = "COMPANY_ID";
}
