package org.dol.database.schema;

import lombok.Data;

import java.util.List;

@Data
public class KeySchema {

    private String keyName;
    private List<ColumnSchema> memberColumns;
}
