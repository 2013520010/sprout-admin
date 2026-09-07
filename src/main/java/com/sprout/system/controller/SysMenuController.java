package com.sprout.system.controller;

import com.sprout.common.result.Result;
import com.sprout.system.entity.SysMenu;
import com.sprout.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单管理
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService menuService;

    @Operation(summary = "查询菜单树")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        return Result.success(menuService.selectMenuTree());
    }

    @Operation(summary = "查询菜单详情")
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/{menuId}")
    public Result<SysMenu> getInfo(@PathVariable Long menuId) {
        return Result.success(menuService.getById(menuId));
    }

    @Operation(summary = "新增菜单")
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysMenu menu) {
        menuService.createMenu(menu);
        return Result.success();
    }

    @Operation(summary = "修改菜单")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysMenu menu) {
        menuService.updateMenu(menu);
        return Result.success();
    }

    @Operation(summary = "删除菜单")
    @PreAuthorize("hasAuthority('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public Result<Void> remove(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return Result.success();
    }
}
