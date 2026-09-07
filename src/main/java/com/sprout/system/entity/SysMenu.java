package com.sprout.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sprout.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 菜单权限表 sys_menu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /** 菜单 ID */
    @TableId
    private Long menuId;

    /** 父菜单 ID（0 为根） */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 菜单类型（M目录 C菜单 F按钮） */
    private String menuType;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 权限标识（如 system:user:list） */
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 显示顺序 */
    private Integer sort;

    /** 是否外链（0否 1是） */
    private String isFrame;

    /** 是否缓存（0否 1是） */
    private String isCache;

    /** 是否显示（0显示 1隐藏） */
    private String visible;

    /** 状态（0正常 1停用） */
    private String status;

    /** 子菜单（非数据库字段） */
    @TableField(exist = false)
    private List<SysMenu> children;
}
