package org.dol.database.schema;

import lombok.Data;

import java.util.List;

@Data
public class IndexSchema {

    private String indexName;
    private boolean isUnique;
    private short type;
    private String order;
    private List<ColumnSchema> memberColumns;

    public String getDisplayNameAndValues(String prefix) {
        final StringBuilder sb = new StringBuilder();
        for (final ColumnSchema columnSchema : memberColumns) {
            sb.append(columnSchema.getDisplayName())
              .append("[\"+")
              .append(prefix)
              .append(columnSchema.getGetter())
              .append("()+\"]");
        }
        return sb.toString();
    }

    public String getDisplayNames() {
        final StringBuilder sb = new StringBuilder();
        for (final ColumnSchema columnSchema : memberColumns) {
            sb.append(columnSchema.getDisplayName()).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }
}
