package org.dol.database.test;

import org.dol.database.schema.DatabaseSchema;
import org.dol.database.schema.DatabaseSchemaLoader;
import org.dol.database.utils.ScriptGenerator;
import org.junit.Test;

public class DatabaseSchemaLoaderTest {

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
