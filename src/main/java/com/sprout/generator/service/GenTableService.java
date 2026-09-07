package com.sprout.generator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sprout.generator.entity.GenTable;

import java.util.List;
import java.util.Map;

/**
 * 代码生成服务
 */
public interface GenTableService extends IService<GenTable> {

    /** 查询可导入的数据库表 */
    List<Map<String, Object>> listDbTables();

    /** 导入表 */
    void importTables(List<String> tableNames);

    /** 分页查询生成配置 */
    IPage<GenTable> pageGenTables(long pageNum, long pageSize, String tableName);

    /** 查询生成配置（含字段） */
    GenTable getGenTable(Long tableId);

    /** 修改生成配置 */
    void updateGenTable(GenTable table);

    /** 删除生成配置 */
    void deleteGenTables(List<Long> tableIds);

    /** 预览代码 */
    Map<String, String> previewCode(Long tableId);

    /** 生成代码（返回 zip 字节流） */
    byte[] generateCode(List<Long> tableIds);
}
