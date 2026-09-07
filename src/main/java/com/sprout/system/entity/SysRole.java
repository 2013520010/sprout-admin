package com.sprout.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sprout.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表 sys_role
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色 ID */
    @TableId
    private Long roleId;

    /** 角色名称 */
    private String roleName;

    /** 角色权限标识（如 admin、common） */
    private String roleKey;

    /** 显示顺序 */
    private Integer sort;

    /** 状态（0正常 1停用） */
    private String status;
}
