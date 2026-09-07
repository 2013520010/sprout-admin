package com.sprout.log.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sprout.common.result.PageResult;
import com.sprout.common.result.Result;
import com.sprout.log.entity.SysLoginLog;
import com.sprout.log.entity.SysOperLog;
import com.sprout.log.mapper.SysLoginLogMapper;
import com.sprout.log.mapper.SysOperLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 日志管理
 */
@Tag(name = "日志管理")
@RestController
@RequestMapping("/monitor/log")
@RequiredArgsConstructor
public class SysLogController {

    private final SysLoginLogMapper loginLogMapper;
    private final SysOperLogMapper operLogMapper;

    @Operation(summary = "分页查询登录日志")
    @PreAuthorize("hasAuthority('monitor:loginlog:list')")
    @GetMapping("/login/list")
    public Result<PageResult<SysLoginLog>> loginList(@RequestParam(defaultValue = "1") long pageNum,
                                                     @RequestParam(defaultValue = "10") long pageSize,
                                                     @RequestParam(required = false) String username) {
        Page<SysLoginLog> page = loginLogMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysLoginLog>()
                        .like(username != null, SysLoginLog::getUsername, username)
                        .orderByDesc(SysLoginLog::getInfoId));
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "分页查询操作日志")
    @PreAuthorize("hasAuthority('monitor:operlog:list')")
    @GetMapping("/oper/list")
    public Result<PageResult<SysOperLog>> operList(@RequestParam(defaultValue = "1") long pageNum,
                                                   @RequestParam(defaultValue = "10") long pageSize,
                                                   @RequestParam(required = false) String operName) {
        Page<SysOperLog> page = operLogMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysOperLog>()
                        .like(operName != null, SysOperLog::getOperName, operName)
                        .orderByDesc(SysOperLog::getOperId));
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "删除操作日志")
    @PreAuthorize("hasAuthority('monitor:operlog:remove')")
    @DeleteMapping("/oper/{operIds}")
    public Result<Void> removeOper(@PathVariable List<Long> operIds) {
        operLogMapper.deleteBatchIds(operIds);
        return Result.success();
    }

    @Operation(summary = "清空操作日志")
    @PreAuthorize("hasAuthority('monitor:operlog:remove')")
    @DeleteMapping("/oper/clean")
    public Result<Void> cleanOper() {
        operLogMapper.delete(null);
        return Result.success();
    }
}
