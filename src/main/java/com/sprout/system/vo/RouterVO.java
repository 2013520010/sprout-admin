package com.sprout.system.vo;

import lombok.Data;

import java.util.List;

/**
 * 前端路由节点（菜单树）
 */
@Data
public class RouterVO {

    /** 菜单 ID */
    private Long id;

    /** 父菜单 ID */
    private Long parentId;

    /** 路由名称 */
    private String name;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 图标 */
    private String icon;

    /** 子路由 */
    private List<RouterVO> children;
}
