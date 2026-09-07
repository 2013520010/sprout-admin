package com.sprout.security;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 登录用户信息（存入 Redis，随 JWT 令牌关联）
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 部门 ID */
    private Long deptId;

    /** 角色标识集合 */
    private Set<String> roles;

    /** 权限标识集合 */
    private Set<String> permissions;

    /** 登录时间 */
    private Long loginTime;
}
