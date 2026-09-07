package com.sprout.system.vo;

import com.sprout.system.entity.SysUser;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * 当前用户信息（含角色、权限）
 */
@Data
@Builder
public class UserInfoVO {

    /** 用户信息 */
    private SysUser user;

    /** 角色标识集合 */
    private Set<String> roles;

    /** 权限标识集合 */
    private Set<String> permissions;
}
