package org.dol.database.utils;

import org.dol.database.schema.ColumnSchema;
import org.dol.database.schema.DataTypeEnum;

/**
 * EasyUI 相关的列渲染辅助方法.
 * <p>
 * 从 ColumnSchema 中抽离，使 ColumnSchema 专注于数据库元数据建模.
 */
public abstract class ColumnUIHelper {

    public static String getEasyUIClassForEdit(ColumnSchema column) {
        if (column.notEditable()) {
            return "easyui-textbox";
        }
        if (column.isStringColumn()) {
            if (column.isNotNull()
                    || column.getColumnName().toLowerCase().endsWith("email")
                    || column.getColumnName().toLowerCase().endsWith("url")) {
                return "easyui-textbox";
            }
        } else if (column.isInteger()) {
            if (column.getColumnName().toLowerCase().endsWith("_time")) {
                return "easyui-datetimebox";
            } else {
                return "easyui-numberbox";
            }
        }
        return "easyui-textbox";
    }

    public static String getEasyUIClassForSearch(ColumnSchema column) {
        if (column.isInteger()) {
            if (column.getColumnName().toLowerCase().endsWith("_time")) {
                return "easyui-datetimebox";
            }
        }
        return "easyui-textbox";
    }

    public static String getEasyUIInputOptionForEdit(ColumnSchema column) {
        if (column.notEditable()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        if (column.isStringColumn()) {
            if (column.isNotNull()) {
                sb.append("required:true,");
            }
            if (column.getPropertyName().toLowerCase().endsWith("url")) {
                sb.append("validType:['url','length[0,").append(column.getColumnSize()).append("]'],");
            } else if (column.getPropertyName().toLowerCase().endsWith("email")) {
                sb.append("validType:['email','length[0,").append(column.getColumnSize()).append("]'],");
            } else {
                sb.append("validType:['length[0,").append(column.getColumnSize()).append("]'],");
            }
        }
        if (sb.length() > 0) {
            final String options = sb.substring(0, sb.length() - 1);
            return "data-options=\"" + options + "\" ";
        }
        return "";
    }

    public static String getThDataOptions(ColumnSchema column) {
        final StringBuilder sb = new StringBuilder("field:'" + column.getPropertyName() + "'");
        if (column.getColumnName().endsWith("_time")) {
            sb.append(",formatter:dateFormatter");
        } else if (column.getDataTypeEnum() == DataTypeEnum.BIT) {
            sb.append(",formatter:yesnoFormatter");
        }
        return sb.toString();
    }

    public static String getDataProvider(ColumnSchema column) {
        if (column.isDateColumn()) {
            return "data-provider=\"datepicker\"";
        }
        return "";
    }

    public static String getDataRender(ColumnSchema column) {
        if (column.isDateColumn() && (column.isIntColumn() || column.isLongColumn())) {
            return "data-render=\"dateRender\"";
        }
        return "";
    }
}
