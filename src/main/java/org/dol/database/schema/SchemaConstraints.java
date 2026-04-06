package org.dol.database.schema;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 列命名约定配置.
 * <p>
 * 默认值覆盖常见命名 (UPDATE_TIME, CREATE_USER, IS_DELETED 等).
 * 可通过 {@code SchemaConstraints.UPDATE_USER_COLUMN.add("EDITOR")} 扩展,
 * 或通过 {@link #reset()} 恢复默认值.
 * <p>
 * 所有集合使用 Set 实现, contains() 为 O(1).
 */
public abstract class SchemaConstraints {

    public static Set<String> UPDATE_USER_COLUMN = newSet(
            "UPDATE_USERID", "UPDATE_USER_ID", "UPDATE_USER",
            "EDIT_USER", "EDITUSER",
            "MODIFY_USER", "UPDATEUSERID", "UPDATEUSER", "MODIFYUSER",
            "UPT_USER", "UPT_USER_ID", "UPTUSER"
    );

    public static Set<String> UPDATE_TIME_COLUMN = newSet(
            "UPDATE_TIME", "UPDATE_DATE", "UPDATETIME", "UPDATEDATE",
            "LAST_UPDATE_TIME", "LAST_UPDATE_DATE"
    );

    public static Set<String> CREATE_USER_COLUMN = newSet(
            "CREATE_USER_ID", "CREATE_USERID", "CREATE_USER", "CREATEUSERID", "CREATEUSER"
    );

    public static Set<String> CREATE_TIME_COLUMN = newSet(
            "CREATE_TIME", "CREATE_DATE", "CREATETIME", "CREATEDATE"
    );

    public static Set<String> DELETED_COLUMN = newSet(
            "IS_DELETED", "DELETE_FLAG", "DELETED",
            "DELETE_TIME", "DELETE_USER", "DELETED_TIME", "DELETED_USER"
    );

    public static Set<String> DELETE_COLUMN = newSet(
            "IS_DELETED", "DELETE_FLAG", "DELETED"
    );

    public static Set<String> VERSION_COLUMN = newSet("VERSION", "V", "VER");

    public static Set<String> STATUS_COLUMN = newSet("STATUS", "STAT");

    public static Set<String> DELETE_USER_COLUMN = newSet(
            "DELETE_USER", "DELETED_USER", "DELETED_BY", "DELETE_BY"
    );

    public static Set<String> DELETE_TIME_COLUMN = newSet("DELETE_TIME", "DELETED_TIME");

    public static Pattern SYMBOL_PATTERN = Pattern.compile("[,，.。:：;；\\s\\-—]");

    public static String COMPANY_ID = "COMPANY_ID";

    private static Set<String> newSet(String... values) {
        return new LinkedHashSet<>(Arrays.asList(values));
    }

    /**
     * 恢复所有约定为默认值.
     */
    public static void reset() {
        UPDATE_USER_COLUMN = newSet(
                "UPDATE_USERID", "UPDATE_USER_ID", "UPDATE_USER",
                "EDIT_USER", "EDITUSER",
                "MODIFY_USER", "UPDATEUSERID", "UPDATEUSER", "MODIFYUSER",
                "UPT_USER", "UPT_USER_ID", "UPTUSER"
        );
        UPDATE_TIME_COLUMN = newSet(
                "UPDATE_TIME", "UPDATE_DATE", "UPDATETIME", "UPDATEDATE",
                "LAST_UPDATE_TIME", "LAST_UPDATE_DATE"
        );
        CREATE_USER_COLUMN = newSet(
                "CREATE_USER_ID", "CREATE_USERID", "CREATE_USER", "CREATEUSERID", "CREATEUSER"
        );
        CREATE_TIME_COLUMN = newSet("CREATE_TIME", "CREATE_DATE", "CREATETIME", "CREATEDATE");
        DELETED_COLUMN = newSet(
                "IS_DELETED", "DELETE_FLAG", "DELETED",
                "DELETE_TIME", "DELETE_USER", "DELETED_TIME", "DELETED_USER"
        );
        DELETE_COLUMN = newSet("IS_DELETED", "DELETE_FLAG", "DELETED");
        VERSION_COLUMN = newSet("VERSION", "V", "VER");
        STATUS_COLUMN = newSet("STATUS", "STAT");
        DELETE_USER_COLUMN = newSet("DELETE_USER", "DELETED_USER", "DELETED_BY", "DELETE_BY");
        DELETE_TIME_COLUMN = newSet("DELETE_TIME", "DELETED_TIME");
        SYMBOL_PATTERN = Pattern.compile("[,，.。:：;；\\s\\-—]");
        COMPANY_ID = "COMPANY_ID";
    }
}
