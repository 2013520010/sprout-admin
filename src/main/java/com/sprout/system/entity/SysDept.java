package com.sprout.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sprout.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 部门表 sys_dept
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /** 部门 ID */
    @TableId
    private Long deptId;

    /** 父部门 ID（0 为根） */
    private Long parentId;

    /** 祖级列表（逗号分隔，如 0,100,200） */
    private String ancestors;

    /** 部门名称 */
    private String deptName;

    /** 显示顺序 */
    private Integer sort;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 状态（0正常 1停用） */
    private String status;

    /** 子部门（非数据库字段） */
    @TableField(exist = false)
    private List<SysDept> children;
}
