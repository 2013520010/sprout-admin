package com.sprout.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sprout.system.entity.SysMenu;

import java.util.List;

/**
 * 菜单服务
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 查询菜单树
     */
    List<SysMenu> selectMenuTree();

    void createMenu(SysMenu menu);

    void updateMenu(SysMenu menu);

    void deleteMenu(Long menuId);
}
