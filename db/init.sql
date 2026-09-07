-- ============================================================
-- 新芽 sprout-admin 数据库初始化脚本
-- MySQL 5.7+ / 8.0
-- 默认管理员账号：admin / admin123
-- ============================================================

CREATE DATABASE IF NOT EXISTS `sprout_admin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `sprout_admin`;

-- ------------------------------------------------------------
-- 1. 部门表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
    `dept_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父部门ID',
    `ancestors`   VARCHAR(255) NOT NULL DEFAULT '0' COMMENT '祖级列表',
    `dept_name`   VARCHAR(30)  NOT NULL COMMENT '部门名称',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `leader`      VARCHAR(20)  DEFAULT NULL COMMENT '负责人',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    `email`       VARCHAR(50)  DEFAULT NULL COMMENT '邮箱',
    `status`      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 COMMENT='部门表';

-- ------------------------------------------------------------
-- 2. 用户表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `user_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(30)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt）',
    `nickname`    VARCHAR(30)  NOT NULL COMMENT '昵称',
    `dept_id`     BIGINT       DEFAULT NULL COMMENT '部门ID',
    `email`       VARCHAR(50)  DEFAULT NULL COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `sex`         CHAR(1)      DEFAULT '0' COMMENT '性别（0男 1女 2未知）',
    `avatar`      VARCHAR(255) DEFAULT '' COMMENT '头像',
    `status`      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `login_ip`    VARCHAR(128) DEFAULT '' COMMENT '最后登录IP',
    `login_date`  DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='用户表';

-- ------------------------------------------------------------
-- 3. 角色表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `role_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   VARCHAR(30)  NOT NULL COMMENT '角色名称',
    `role_key`    VARCHAR(100) NOT NULL COMMENT '角色权限标识',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `status`      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志',
    PRIMARY KEY (`role_id`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='角色表';

-- ------------------------------------------------------------
-- 4. 菜单表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `menu_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父菜单ID',
    `menu_name`   VARCHAR(50)  NOT NULL COMMENT '菜单名称',
    `menu_type`   CHAR(1)      NOT NULL DEFAULT 'C' COMMENT '类型（M目录 C菜单 F按钮）',
    `path`        VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    `component`   VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `perms`       VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `icon`        VARCHAR(100) DEFAULT '#' COMMENT '图标',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `is_frame`    CHAR(1)      NOT NULL DEFAULT '0' COMMENT '是否外链',
    `is_cache`    CHAR(1)      NOT NULL DEFAULT '0' COMMENT '是否缓存',
    `visible`     CHAR(1)      NOT NULL DEFAULT '0' COMMENT '是否显示',
    `status`      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志',
    PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2000 COMMENT='菜单权限表';

-- ------------------------------------------------------------
-- 5. 用户-角色关联表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB COMMENT='用户-角色关联表';

-- ------------------------------------------------------------
-- 6. 角色-菜单关联表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB COMMENT='角色-菜单关联表';

-- ------------------------------------------------------------
-- 7. 登录日志表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
    `info_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `username`   VARCHAR(50)  DEFAULT '' COMMENT '用户名',
    `ip`         VARCHAR(128) DEFAULT '' COMMENT '登录IP',
    `browser`    VARCHAR(50)  DEFAULT '' COMMENT '浏览器',
    `os`         VARCHAR(50)  DEFAULT '' COMMENT '操作系统',
    `status`     CHAR(1)      DEFAULT '0' COMMENT '状态（0成功 1失败）',
    `msg`        VARCHAR(255) DEFAULT '' COMMENT '提示消息',
    `login_time` DATETIME     DEFAULT NULL COMMENT '登录时间',
    PRIMARY KEY (`info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='登录日志表';

-- ------------------------------------------------------------
-- 8. 操作日志表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
    `oper_id`        BIGINT        NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `title`          VARCHAR(50)   DEFAULT '' COMMENT '操作模块',
    `business_type`  INT           DEFAULT 0 COMMENT '业务类型',
    `method`         VARCHAR(200)  DEFAULT '' COMMENT '请求方法',
    `request_method` VARCHAR(10)   DEFAULT '' COMMENT '请求方式',
    `oper_name`      VARCHAR(50)   DEFAULT '' COMMENT '操作人员',
    `oper_url`       VARCHAR(255)  DEFAULT '' COMMENT '请求URL',
    `oper_ip`        VARCHAR(128)  DEFAULT '' COMMENT '操作IP',
    `oper_param`     VARCHAR(2000) DEFAULT '' COMMENT '请求参数',
    `json_result`    VARCHAR(2000) DEFAULT '' COMMENT '返回结果',
    `status`         INT           DEFAULT 0 COMMENT '状态（0成功 1失败）',
    `error_msg`      VARCHAR(2000) DEFAULT '' COMMENT '错误消息',
    `oper_time`      DATETIME      DEFAULT NULL COMMENT '操作时间',
    `cost_time`      BIGINT        DEFAULT 0 COMMENT '耗时（毫秒）',
    PRIMARY KEY (`oper_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='操作日志表';

-- ------------------------------------------------------------
-- 9. 代码生成-业务表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table` (
    `table_id`        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `table_name`      VARCHAR(200) NOT NULL COMMENT '表名称',
    `table_comment`   VARCHAR(200) DEFAULT '' COMMENT '表描述',
    `class_name`      VARCHAR(100) DEFAULT '' COMMENT '实体类名称',
    `package_name`    VARCHAR(100) DEFAULT 'com.sprout' COMMENT '生成包路径',
    `module_name`     VARCHAR(30)  DEFAULT 'business' COMMENT '生成模块名',
    `business_name`   VARCHAR(30)  DEFAULT '' COMMENT '生成业务名',
    `function_name`   VARCHAR(50)  DEFAULT '' COMMENT '生成功能名',
    `function_author` VARCHAR(50)  DEFAULT '' COMMENT '生成作者',
    `options`         VARCHAR(1000) DEFAULT NULL COMMENT '其它选项',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`     DATETIME     DEFAULT NULL COMMENT '更新时间',
    `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志',
    PRIMARY KEY (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='代码生成业务表';

-- ------------------------------------------------------------
-- 10. 代码生成-业务表字段
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column` (
    `column_id`      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `table_id`       BIGINT       NOT NULL COMMENT '归属表编号',
    `column_name`    VARCHAR(200) DEFAULT '' COMMENT '列名称',
    `column_comment` VARCHAR(500) DEFAULT '' COMMENT '列描述',
    `column_type`    VARCHAR(100) DEFAULT '' COMMENT '列类型',
    `java_type`      VARCHAR(100) DEFAULT '' COMMENT 'Java类型',
    `java_field`     VARCHAR(100) DEFAULT '' COMMENT 'Java字段名',
    `is_pk`          CHAR(1)      DEFAULT '0' COMMENT '是否主键',
    `is_increment`   CHAR(1)      DEFAULT '0' COMMENT '是否自增',
    `is_required`    CHAR(1)      DEFAULT '0' COMMENT '是否必填',
    `is_insert`      CHAR(1)      DEFAULT '1' COMMENT '是否插入字段',
    `is_edit`        CHAR(1)      DEFAULT '1' COMMENT '是否编辑字段',
    `is_list`        CHAR(1)      DEFAULT '1' COMMENT '是否列表字段',
    `is_query`       CHAR(1)      DEFAULT '1' COMMENT '是否查询字段',
    `query_type`     VARCHAR(20)  DEFAULT 'EQ' COMMENT '查询方式',
    `html_type`      VARCHAR(20)  DEFAULT 'input' COMMENT '显示类型',
    `dict_type`      VARCHAR(200) DEFAULT '' COMMENT '字典类型',
    `sort`           INT          DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (`column_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='代码生成业务表字段';

-- ============================================================
-- 初始数据
-- ============================================================

-- 部门
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `sort`, `leader`, `status`, `create_time`, `deleted`) VALUES
(100, 0,   '0',     '新芽科技',   0, 'admin', '0', NOW(), 0),
(101, 100, '0,100', '研发部',     1, 'admin', '0', NOW(), 0),
(102, 100, '0,100', '产品部',     2, 'admin', '0', NOW(), 0),
(103, 101, '0,100,101', '后端组',  1, 'admin', '0', NOW(), 0);

-- 角色
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `sort`, `status`, `create_time`, `deleted`) VALUES
(1, '超级管理员', 'admin',  1, '0', NOW(), 0),
(2, '普通用户',   'common', 2, '0', NOW(), 0);

-- 用户（admin / admin123）
INSERT INTO `sys_user` (`user_id`, `username`, `password`, `nickname`, `dept_id`, `email`, `phone`, `sex`, `status`, `create_time`, `deleted`) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '管理员', 100, 'admin@sprout.com', '15888888888', '0', '0', NOW(), 0);

-- 用户-角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 菜单（目录/菜单/按钮）
INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort`, `status`, `create_time`, `deleted`) VALUES
-- 系统管理
(1,    0,   '系统管理', 'M', '/system', NULL, NULL, 'system', 1, '0', NOW(), 0),
(100,  1,   '用户管理', 'C', '/system/user', 'system/user/index', 'system:user:list', 'user', 1, '0', NOW(), 0),
(1001, 100, '用户查询', 'F', NULL, NULL, 'system:user:query', NULL, 1, '0', NOW(), 0),
(1002, 100, '用户新增', 'F', NULL, NULL, 'system:user:add', NULL, 2, '0', NOW(), 0),
(1003, 100, '用户修改', 'F', NULL, NULL, 'system:user:edit', NULL, 3, '0', NOW(), 0),
(1004, 100, '用户删除', 'F', NULL, NULL, 'system:user:remove', NULL, 4, '0', NOW(), 0),
(1005, 100, '重置密码', 'F', NULL, NULL, 'system:user:resetPwd', NULL, 5, '0', NOW(), 0),
(101,  1,   '角色管理', 'C', '/system/role', 'system/role/index', 'system:role:list', 'peoples', 2, '0', NOW(), 0),
(1011, 101, '角色查询', 'F', NULL, NULL, 'system:role:query', NULL, 1, '0', NOW(), 0),
(1012, 101, '角色新增', 'F', NULL, NULL, 'system:role:add', NULL, 2, '0', NOW(), 0),
(1013, 101, '角色修改', 'F', NULL, NULL, 'system:role:edit', NULL, 3, '0', NOW(), 0),
(1014, 101, '角色删除', 'F', NULL, NULL, 'system:role:remove', NULL, 4, '0', NOW(), 0),
(102,  1,   '菜单管理', 'C', '/system/menu', 'system/menu/index', 'system:menu:list', 'tree-table', 3, '0', NOW(), 0),
(1021, 102, '菜单查询', 'F', NULL, NULL, 'system:menu:query', NULL, 1, '0', NOW(), 0),
(1022, 102, '菜单新增', 'F', NULL, NULL, 'system:menu:add', NULL, 2, '0', NOW(), 0),
(1023, 102, '菜单修改', 'F', NULL, NULL, 'system:menu:edit', NULL, 3, '0', NOW(), 0),
(1024, 102, '菜单删除', 'F', NULL, NULL, 'system:menu:remove', NULL, 4, '0', NOW(), 0),
(103,  1,   '部门管理', 'C', '/system/dept', 'system/dept/index', 'system:dept:list', 'tree', 4, '0', NOW(), 0),
(1031, 103, '部门查询', 'F', NULL, NULL, 'system:dept:query', NULL, 1, '0', NOW(), 0),
(1032, 103, '部门新增', 'F', NULL, NULL, 'system:dept:add', NULL, 2, '0', NOW(), 0),
(1033, 103, '部门修改', 'F', NULL, NULL, 'system:dept:edit', NULL, 3, '0', NOW(), 0),
(1034, 103, '部门删除', 'F', NULL, NULL, 'system:dept:remove', NULL, 4, '0', NOW(), 0),
-- 日志管理
(2,    0,   '日志管理', 'M', '/monitor', NULL, NULL, 'log', 2, '0', NOW(), 0),
(200,  2,   '登录日志', 'C', '/monitor/loginlog', 'monitor/loginlog/index', 'monitor:loginlog:list', 'logininfor', 1, '0', NOW(), 0),
(201,  2,   '操作日志', 'C', '/monitor/operlog', 'monitor/operlog/index', 'monitor:operlog:list', 'form', 2, '0', NOW(), 0),
(2011, 201, '删除日志', 'F', NULL, NULL, 'monitor:operlog:remove', NULL, 1, '0', NOW(), 0),
-- 代码生成
(3,    0,   '开发工具', 'M', '/tool', NULL, NULL, 'tool', 3, '0', NOW(), 0),
(300,  3,   '代码生成', 'C', '/tool/gen', 'tool/gen/index', 'tool:gen:list', 'code', 1, '0', NOW(), 0),
(3001, 300, '导入表', 'F', NULL, NULL, 'tool:gen:import', NULL, 1, '0', NOW(), 0),
(3002, 300, '查询配置', 'F', NULL, NULL, 'tool:gen:query', NULL, 2, '0', NOW(), 0),
(3003, 300, '修改配置', 'F', NULL, NULL, 'tool:gen:edit', NULL, 3, '0', NOW(), 0),
(3004, 300, '删除配置', 'F', NULL, NULL, 'tool:gen:remove', NULL, 4, '0', NOW(), 0),
(3005, 300, '预览代码', 'F', NULL, NULL, 'tool:gen:preview', NULL, 5, '0', NOW(), 0),
(3006, 300, '生成代码', 'F', NULL, NULL, 'tool:gen:code', NULL, 6, '0', NOW(), 0);

-- 角色-菜单（admin 拥有全部菜单）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `menu_id` FROM `sys_menu`;
