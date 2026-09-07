package com.sprout.generator.util;

import com.sprout.generator.entity.GenTableColumn;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 代码生成工具类
 */
public final class GenUtils {

    private GenUtils() {
    }

    /**
     * 数据库列信息 -> 生成字段信息
     */
    public static GenTableColumn toColumn(Map<String, Object> map) {
        GenTableColumn column = new GenTableColumn();
        String columnName = String.valueOf(map.get("columnName"));
        String dataType = String.valueOf(map.get("dataType"));
        String columnKey = String.valueOf(map.get("columnKey"));

        String javaField = toCamelCase(columnName);
        column.setColumnName(columnName);
        column.setColumnComment(String.valueOf(map.get("columnComment")));
        column.setColumnType(String.valueOf(map.get("columnType")));
        column.setJavaType(getJavaType(dataType));
        column.setJavaField(javaField);
        column.setCapJavaField(capitalize(javaField));
        column.setIsPk("PRI".equalsIgnoreCase(columnKey) ? "1" : "0");
        column.setIsIncrement("auto_increment".equalsIgnoreCase(String.valueOf(map.get("extra"))) ? "1" : "0");
        column.setIsRequired("NO".equalsIgnoreCase(String.valueOf(map.get("isNullable"))) ? "1" : "0");
        column.setIsInsert("1");
        column.setIsEdit("1");
        column.setIsList("1");
        // 主键不做查询
        column.setIsQuery("PRI".equalsIgnoreCase(columnKey) ? "0" : "1");
        column.setQueryType("EQ");
        column.setHtmlType("input");
        Object sortValue = map.get("ordinalPosition");
        column.setSort(sortValue == null ? 0 : ((Number) sortValue).intValue());
        return column;
    }

    /**
     * 数据库类型 -> Java 类型
     */
    private static String getJavaType(String dataType) {
        return switch (dataType.toLowerCase(Locale.ROOT)) {
            case "bigint" -> "Long";
            case "int", "integer", "smallint", "tinyint" -> "Integer";
            case "decimal", "numeric" -> "BigDecimal";
            case "double" -> "Double";
            case "float" -> "Float";
            case "datetime", "timestamp" -> "LocalDateTime";
            case "date" -> "LocalDate";
            case "bit", "boolean" -> "Boolean";
            case "text", "longtext" -> "String";
            default -> "String";
        };
    }

    /**
     * 下划线转驼峰
     */
    public static String toCamelCase(String columnName) {
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        for (char c : columnName.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else if (upper) {
                sb.append(Character.toUpperCase(c));
                upper = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 表名 -> 类名（去掉前缀 sys_/t_ 后驼峰）
     */
    public static String toClassName(String tableName) {
        String name = tableName;
        if (name.startsWith("sys_")) {
            name = name.substring(4);
        } else if (name.startsWith("t_")) {
            name = name.substring(2);
        }
        return capitalize(toCamelCase(name));
    }

    /**
     * 过滤掉基类字段（公共字段由 BaseEntity 继承）
     */
    public static List<GenTableColumn> filterBaseColumns(List<GenTableColumn> columns) {
        List<String> baseFields = List.of("create_by", "create_time", "update_by", "update_time", "remark", "deleted");
        return columns.stream()
                .filter(c -> !baseFields.contains(c.getColumnName()))
                .toList();
    }
}
