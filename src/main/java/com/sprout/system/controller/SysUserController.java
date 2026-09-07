package com.sprout.system.controller;

import com.sprout.common.result.PageResult;
import com.sprout.common.result.Result;
import com.sprout.log.annotation.Log;
import com.sprout.system.entity.SysUser;
import com.sprout.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import java.util.Map;

/**
 * 用户管理
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @Operation(summary = "分页查询用户")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(@RequestParam(defaultValue = "1") long pageNum,
                                            @RequestParam(defaultValue = "10") long pageSize,
                                            @RequestParam(required = false) String username,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(required = false) Long deptId) {
        return Result.success(PageResult.of(userService.pageUsers(pageNum, pageSize, username, status, deptId)));
    }

    @Operation(summary = "查询用户详情")
    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping("/{userId}")
    public Result<SysUser> getInfo(@PathVariable Long userId) {
        return Result.success(userService.getById(userId));
    }

    @Operation(summary = "新增用户")
    @Log(title = "用户管理", businessType = 1)
    @PreAuthorize("hasAuthority('system:user:add')")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SysUser user) {
        userService.createUser(user);
        return Result.success();
    }

    @Operation(summary = "修改用户")
    @Log(title = "用户管理", businessType = 2)
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysUser user) {
        userService.updateUser(user);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @Log(title = "用户管理", businessType = 3)
    @PreAuthorize("hasAuthority('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public Result<Void> remove(@PathVariable List<Long> userIds) {
        userService.deleteUsers(userIds);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public Result<Void> resetPwd(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String password = body.get("password") == null ? null : body.get("password").toString();
        userService.resetPassword(userId, password);
        return Result.success();
    }

    @Operation(summary = "查询用户已有角色")
    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping("/{userId}/roles")
    public Result<List<Long>> getUserRoles(@PathVariable Long userId) {
        return Result.success(userService.getUserRoleIds(userId));
    }

    @Operation(summary = "分配角色")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/{userId}/roles")
    public Result<Void> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return Result.success();
    }
}
