package com.sprout.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sprout.system.entity.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单 Mapper
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询用户拥有的菜单（用于构建路由，目录 + 菜单）
     */
    @Select("SELECT DISTINCT m.* FROM sys_menu m " +
            "JOIN sys_role_menu rm ON m.menu_id = rm.menu_id " +
            "JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = '0' AND m.menu_type IN ('M', 'C') " +
            "ORDER BY m.parent_id, m.sort")
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 查询全部权限标识（超级管理员使用）
     */
    @Select("SELECT perms FROM sys_menu WHERE status = '0' AND perms IS NOT NULL AND perms != ''")
    List<String> selectAllPerms();
}
