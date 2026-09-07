package com.sprout.common.constant;

/**
 * 通用常量
 */
public final class Constants {

    private Constants() {
    }

    /** 超级管理员角色标识 */
    public static final String SUPER_ADMIN_ROLE = "admin";

    /** 超级管理员用户 ID */
    public static final Long SUPER_ADMIN_USER_ID = 1L;

    /** 通用是/否 */
    public static final String YES = "1";
    public static final String NO = "0";

    /** 正常状态 */
    public static final String STATUS_NORMAL = "0";
    /** 停用状态 */
    public static final String STATUS_DISABLE = "1";

    /** 菜单类型：目录/菜单/按钮 */
    public static final String MENU_TYPE_DIR = "M";
    public static final String MENU_TYPE_MENU = "C";
    public static final String MENU_TYPE_BUTTON = "F";
}
