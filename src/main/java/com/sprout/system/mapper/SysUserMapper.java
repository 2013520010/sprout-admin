package com.sprout.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sprout.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户 Mapper
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询用户的角色标识集合
     */
    @Select("SELECT r.role_key FROM sys_role r " +
            "JOIN sys_user_role ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = '0' AND r.deleted = 0")
    List<String> selectRoleKeysByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的菜单权限标识集合
     */
    @Select("SELECT DISTINCT m.perms FROM sys_menu m " +
            "JOIN sys_role_menu rm ON m.menu_id = rm.menu_id " +
            "JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = '0' AND m.perms IS NOT NULL AND m.perms != ''")
    List<String> selectMenuPermsByUserId(@Param("userId") Long userId);
}
