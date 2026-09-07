package com.sprout.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sprout.system.entity.SysRole;

import java.util.List;

/**
 * 角色服务
 */
public interface SysRoleService extends IService<SysRole> {

    IPage<SysRole> pageRoles(long pageNum, long pageSize, String roleName, String status);

    void createRole(SysRole role);

    void updateRole(SysRole role);

    void deleteRoles(List<Long> roleIds);

    List<Long> getRoleMenuIds(Long roleId);

    void assignMenus(Long roleId, List<Long> menuIds);
}
