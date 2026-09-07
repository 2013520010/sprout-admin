package com.sprout.system.controller;

import com.sprout.common.result.PageResult;
import com.sprout.common.result.Result;
import com.sprout.system.entity.SysRole;
import com.sprout.system.service.SysRoleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色管理
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @Operation(summary = "分页查询角色")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/list")
    public Result<PageResult<SysRole>> list(@RequestParam(defaultValue = "1") long pageNum,
                                            @RequestParam(defaultValue = "10") long pageSize,
                                            @RequestParam(required = false) String roleName,
                                            @RequestParam(required = false) String status) {
        return Result.success(PageResult.of(roleService.pageRoles(pageNum, pageSize, roleName, status)));
    }

    @Operation(summary = "查询全部角色（下拉框用）")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/all")
    public Result<List<SysRole>> all() {
        return Result.success(roleService.list());
    }

    @Operation(summary = "新增角色")
    @PreAuthorize("hasAuthority('system:role:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        roleService.createRole(role);
        return Result.success();
    }

    @Operation(summary = "修改角色")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysRole role) {
        roleService.updateRole(role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @PreAuthorize("hasAuthority('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public Result<Void> remove(@PathVariable List<Long> roleIds) {
        roleService.deleteRoles(roleIds);
        return Result.success();
    }

    @Operation(summary = "查询角色已有菜单")
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/{roleId}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long roleId) {
        return Result.success(roleService.getRoleMenuIds(roleId));
    }

    @Operation(summary = "分配菜单权限")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping("/{roleId}/menus")
    public Result<Void> assignMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.success();
    }
}
