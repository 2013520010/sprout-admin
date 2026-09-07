package com.sprout.generator.controller;

import com.sprout.common.result.PageResult;
import com.sprout.common.result.Result;
import com.sprout.generator.entity.GenTable;
import com.sprout.generator.service.GenTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
 * 代码生成
 */
@Tag(name = "代码生成")
@RestController
@RequestMapping("/tool/gen")
@RequiredArgsConstructor
public class GenTableController {

    private final GenTableService genTableService;

    @Operation(summary = "查询可导入的数据库表")
    @PreAuthorize("hasAuthority('tool:gen:list')")
    @GetMapping("/db/list")
    public Result<List<Map<String, Object>>> dbList() {
        return Result.success(genTableService.listDbTables());
    }

    @Operation(summary = "导入表")
    @PreAuthorize("hasAuthority('tool:gen:import')")
    @PostMapping("/import")
    public Result<Void> importTable(@RequestBody List<String> tableNames) {
        genTableService.importTables(tableNames);
        return Result.success();
    }

    @Operation(summary = "分页查询生成配置")
    @PreAuthorize("hasAuthority('tool:gen:list')")
    @GetMapping("/list")
    public Result<PageResult<GenTable>> list(@RequestParam(defaultValue = "1") long pageNum,
                                             @RequestParam(defaultValue = "10") long pageSize,
                                             @RequestParam(required = false) String tableName) {
        return Result.success(PageResult.of(genTableService.pageGenTables(pageNum, pageSize, tableName)));
    }

    @Operation(summary = "查询生成配置详情")
    @PreAuthorize("hasAuthority('tool:gen:query')")
    @GetMapping("/{tableId}")
    public Result<GenTable> getInfo(@PathVariable Long tableId) {
        return Result.success(genTableService.getGenTable(tableId));
    }

    @Operation(summary = "修改生成配置")
    @PreAuthorize("hasAuthority('tool:gen:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody GenTable table) {
        genTableService.updateGenTable(table);
        return Result.success();
    }

    @Operation(summary = "删除生成配置")
    @PreAuthorize("hasAuthority('tool:gen:remove')")
    @DeleteMapping("/{tableIds}")
    public Result<Void> remove(@PathVariable List<Long> tableIds) {
        genTableService.deleteGenTables(tableIds);
        return Result.success();
    }

    @Operation(summary = "预览代码")
    @PreAuthorize("hasAuthority('tool:gen:preview')")
    @GetMapping("/preview/{tableId}")
    public Result<Map<String, String>> preview(@PathVariable Long tableId) {
        return Result.success(genTableService.previewCode(tableId));
    }

    @Operation(summary = "生成代码并下载")
    @PreAuthorize("hasAuthority('tool:gen:code')")
    @GetMapping("/download/{tableIds}")
    public ResponseEntity<byte[]> download(@PathVariable List<Long> tableIds) {
        byte[] data = genTableService.generateCode(tableIds);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sprout-code.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
