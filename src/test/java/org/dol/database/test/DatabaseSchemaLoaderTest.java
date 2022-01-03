package org.dol.database.test;

import org.dol.database.schema.DatabaseSchema;
import org.dol.database.schema.DatabaseSchemaLoader;
import org.dol.database.schema.TableSchema;
import org.junit.Test;

public class DatabaseSchemaLoaderTest {

    @Test
    public void test() {
        DatabaseSchema databaseSchema = DatabaseSchemaLoader.load(
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://localhost:3306/basedata_db?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false",
                "root",
                "9cefB9pT1nTVHg9c",
                "t_"
        );
        for (TableSchema ta : databaseSchema.getTables()) {
            System.out.println(ta.getModelName());
        }
    }
}
