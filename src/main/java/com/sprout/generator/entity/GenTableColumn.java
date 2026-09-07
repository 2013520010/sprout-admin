package com.sprout.generator.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 代码生成业务表字段 gen_table_column
 */
@Data
@TableName("gen_table_column")
public class GenTableColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 编号 */
    @TableId
    private Long columnId;

    /** 归属表编号 */
    private Long tableId;

    /** 列名称 */
    private String columnName;

    /** 列描述 */
    private String columnComment;

    /** 列类型 */
    private String columnType;

    /** Java 类型 */
    private String javaType;

    /** Java 字段名 */
    private String javaField;

    /** 是否主键（1是） */
    private String isPk;

    /** 是否自增（1是） */
    private String isIncrement;

    /** 是否必填（1是） */
    private String isRequired;

    /** 是否为插入字段（1是） */
    private String isInsert;

    /** 是否编辑字段（1是） */
    private String isEdit;

    /** 是否列表字段（1是） */
    private String isList;

    /** 是否查询字段（1是） */
    private String isQuery;

    /** 查询方式（EQ/LIKE/RANGE） */
    private String queryType;

    /** 显示类型（input/textarea/select/radio/datetime） */
    private String htmlType;

    /** 字典类型 */
    private String dictType;

    /** 排序 */
    private Integer sort;

    /** 首字母大写字段名（非数据库字段） */
    @TableField(exist = false)
    private String capJavaField;
}
