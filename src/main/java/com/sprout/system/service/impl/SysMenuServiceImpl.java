package com.sprout.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sprout.common.constant.Constants;
import com.sprout.common.exception.BusinessException;
import com.sprout.system.entity.SysMenu;
import com.sprout.system.entity.SysRoleMenu;
import com.sprout.system.mapper.SysMenuMapper;
import com.sprout.system.mapper.SysRoleMenuMapper;
import com.sprout.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜单服务实现
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<SysMenu> selectMenuTree() {
        List<SysMenu> all = list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getParentId, SysMenu::getSort));
        return buildTree(all, 0L);
    }

    @Override
    public void createMenu(SysMenu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(Constants.STATUS_NORMAL);
        }
        save(menu);
    }

    @Override
    public void updateMenu(SysMenu menu) {
        if (menu.getMenuId().equals(menu.getParentId())) {
            throw new BusinessException("父菜单不能选择自己");
        }
        updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long menuId) {
        // 存在子菜单不允许删除
        if (count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, menuId)) > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }
        removeById(menuId);
        // 清理角色-菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, menuId));
    }

    private List<SysMenu> buildTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .peek(m -> m.setChildren(buildTree(menus, m.getMenuId())))
                .toList();
    }
}
