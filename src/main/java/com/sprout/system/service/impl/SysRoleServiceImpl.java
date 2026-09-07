package com.sprout.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sprout.common.constant.Constants;
import com.sprout.common.exception.BusinessException;
import com.sprout.system.entity.SysRole;
import com.sprout.system.entity.SysRoleMenu;
import com.sprout.system.entity.SysUserRole;
import com.sprout.system.mapper.SysRoleMapper;
import com.sprout.system.mapper.SysRoleMenuMapper;
import com.sprout.system.mapper.SysUserRoleMapper;
import com.sprout.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 角色服务实现
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper roleMenuMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public IPage<SysRole> pageRoles(long pageNum, long pageSize, String roleName, String status) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
                .eq(StringUtils.hasText(status), SysRole::getStatus, status)
                .orderByAsc(SysRole::getSort);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void createRole(SysRole role) {
        checkRoleKeyUnique(role.getRoleKey(), null);
        if (role.getStatus() == null) {
            role.setStatus(Constants.STATUS_NORMAL);
        }
        save(role);
    }

    @Override
    public void updateRole(SysRole role) {
        checkRoleKeyUnique(role.getRoleKey(), role.getRoleId());
        updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoles(List<Long> roleIds) {
        for (Long roleId : roleIds) {
            SysRole role = getById(roleId);
            if (role != null && Constants.SUPER_ADMIN_ROLE.equals(role.getRoleKey())) {
                throw new BusinessException("超级管理员角色不允许删除");
            }
        }
        removeByIds(roleIds);
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds));
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, roleIds));
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuMapper.selectList(
                        new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId))
                .stream().map(SysRoleMenu::getMenuId).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds != null && !menuIds.isEmpty()) {
            menuIds.forEach(menuId -> {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                roleMenuMapper.insert(rm);
            });
        }
    }

    private void checkRoleKeyUnique(String roleKey, Long roleId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleKey, roleKey)
                .ne(roleId != null, SysRole::getRoleId, roleId);
        if (count(wrapper) > 0) {
            throw new BusinessException("角色标识已存在");
        }
    }
}
