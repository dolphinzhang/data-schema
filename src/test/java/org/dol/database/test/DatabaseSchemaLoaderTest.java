package org.dol.database.test;

import org.dol.database.schema.*;
import org.dol.database.utils.ScriptGenerator;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSchemaLoaderTest {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String HOST = "10.12.22.73:3306";
    private static final String USER = "root";
    private static final String PWD = "tvu1p2ack3";

    private static String jdbcUrl(String db) {
        return "jdbc:mysql://" + HOST + "/" + db + "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false";
    }

    /**
     * 遍历 10.12.22.73 上所有数据库的所有表，验证 schema 解析无异常，
     * 并检查是否存在 DataTypeEnum 未覆盖的列类型.
     */
    @Test
    public void testAllDatabases() throws Exception {
        Class.forName(DRIVER);
        List<String> databases = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl("information_schema"), USER, PWD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA " +
                     "WHERE SCHEMA_NAME NOT IN ('mysql','information_schema','performance_schema','sys')")) {
            while (rs.next()) {
                databases.add(rs.getString(1));
            }
        }
        System.out.println("Found " + databases.size() + " databases");

        List<String> unmappedTypes = new ArrayList<>();
        int totalTables = 0;

        for (String db : databases) {
            try {
                DatabaseSchema schema = DatabaseSchemaLoader.load(DRIVER, jdbcUrl(db), USER, PWD, "", true);
                for (TableSchema table : schema.getTables()) {
                    totalTables++;
                    for (ColumnSchema col : table.getColumns()) {
                        if (col.getDataTypeEnum() == null) {
                            String msg = db + "." + table.getTableName() + "." + col.getColumnName()
                                    + " -> TYPE_NAME=" + col.getDataTypeName();
                            unmappedTypes.add(msg);
                        }
                    }
                }
                String ddl = ScriptGenerator.generate(schema);
                assert ddl != null && !ddl.isEmpty() : "DDL generation failed for " + db;
                System.out.println("[OK] " + db + " (" + schema.getTables().size() + " tables)");
            } catch (Exception ex) {
                System.err.println("[FAIL] " + db + " -> " + ex.getMessage());
                throw ex;
            }
        }

        System.out.println("\nTotal: " + totalTables + " tables across " + databases.size() + " databases");
        if (unmappedTypes.isEmpty()) {
            System.out.println("All column types are mapped.");
        } else {
            System.out.println("Unmapped types (" + unmappedTypes.size() + "):");
            unmappedTypes.forEach(s -> System.out.println("  " + s));
            throw new AssertionError(unmappedTypes.size() + " columns have unmapped data types");
        }
    }

    @Test
    public void test() {
        DatabaseSchema databaseSchema = DatabaseSchemaLoader.load(
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://10.12.22.73:3306/producer_pro?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false",
                "root",
                "tvu1p2ack3",
                "t_"
        );


        //String s = JSON.toJSONString(databaseSchema);

        // databaseSchema = JSON.parseObject(s, DatabaseSchema.class);
       /* for (TableSchema ta : databaseSchema.getTables()) {
            System.out.println(ta.getModelName());
        }*/

        String generate = ScriptGenerator.generate(databaseSchema);
        System.out.println(generate);

    }

    @Test
    public void test3() {
        DatabaseSchema databaseSchema = DatabaseSchemaLoader.load(
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://10.12.22.73:3306/producer_pro?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false",
                "root",
                "tvu1p2ack3",
                null,
                "tvu-drive",
                "t_",
                false
        );
        String generate = ScriptGenerator.generate(databaseSchema);
        System.out.println(generate);
    }

    @Test
    public void test2() {
        DatabaseSchema fromDB = DatabaseSchemaLoader.load(
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://10.12.22.73:3306/tvu_drive?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false",
                "root",
                "tvu1p2ack3",
                "t_"
        );
        /*DatabaseSchema toDB = DatabaseSchemaLoader.load(
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false",
                "root",
                "9cefB9pT1nTVHg9c",
                "t_"
        );*/

       /* for (TableSchema ta : databaseSchema.getTables()) {
            System.out.println(ta.getModelName());
        }*/
        DatabaseSchema toDB = DatabaseSchemaLoader.load(
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://10.12.22.73:3306/tvu_drive_prd_20220624?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false",
                "root",
                "tvu1p2ack3",
                "t_"
        );
        String generate = ScriptGenerator.generateModifySQL(fromDB, toDB, false);
        System.out.println(generate);

    }
}
