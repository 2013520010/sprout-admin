package com.sprout.system.controller;

import com.sprout.common.result.Result;
import com.sprout.system.entity.SysDept;
import com.sprout.system.service.SysDeptService;
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
 * 部门管理
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService deptService;

    @Operation(summary = "查询部门树")
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/tree")
    public Result<List<SysDept>> tree() {
        return Result.success(deptService.selectDeptTree());
    }

    @Operation(summary = "查询部门详情")
    @PreAuthorize("hasAuthority('system:dept:query')")
    @GetMapping("/{deptId}")
    public Result<SysDept> getInfo(@PathVariable Long deptId) {
        return Result.success(deptService.getById(deptId));
    }

    @Operation(summary = "新增部门")
    @PreAuthorize("hasAuthority('system:dept:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysDept dept) {
        deptService.createDept(dept);
        return Result.success();
    }

    @Operation(summary = "修改部门")
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysDept dept) {
        deptService.updateDept(dept);
        return Result.success();
    }

    @Operation(summary = "删除部门")
    @PreAuthorize("hasAuthority('system:dept:remove')")
    @DeleteMapping("/{deptId}")
    public Result<Void> remove(@PathVariable Long deptId) {
        deptService.deleteDept(deptId);
        return Result.success();
    }
}
