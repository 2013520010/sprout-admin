package com.sprout.generator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sprout.common.exception.BusinessException;
import com.sprout.generator.entity.GenTable;
import com.sprout.generator.entity.GenTableColumn;
import com.sprout.generator.mapper.GenTableColumnMapper;
import com.sprout.generator.mapper.GenTableMapper;
import com.sprout.generator.service.GenTableService;
import com.sprout.generator.util.GenUtils;
import lombok.RequiredArgsConstructor;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成服务实现
 */
@Service
@RequiredArgsConstructor
public class GenTableServiceImpl extends ServiceImpl<GenTableMapper, GenTable> implements GenTableService {

    private final GenTableColumnMapper columnMapper;

    /** Velocity 引擎（classpath 加载模板） */
    private static final VelocityEngine VELOCITY_ENGINE;

    static {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(VelocityEngine.RESOURCE_LOADERS, "classpath");
        engine.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        engine.init();
        VELOCITY_ENGINE = engine;
    }

    /** 模板文件名 */
    private static final List<String> TEMPLATES = List.of(
            "entity.java.vm", "mapper.java.vm", "service.java.vm", "serviceImpl.java.vm", "controller.java.vm");

    @Override
    public List<Map<String, Object>> listDbTables() {
        return baseMapper.selectDbTableList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importTables(List<String> tableNames) {
        for (String tableName : tableNames) {
            if (baseMapper.countDbTable(tableName) == 0) {
                continue;
            }
            // 已导入则跳过
            if (count(new LambdaQueryWrapper<GenTable>().eq(GenTable::getTableName, tableName)) > 0) {
                continue;
            }
            GenTable table = new GenTable();
            table.setTableName(tableName);
            List<Map<String, Object>> columnMaps = baseMapper.selectDbTableColumns(tableName);
            String comment = tableName;
            if (!columnMaps.isEmpty()) {
                comment = String.valueOf(columnMaps.get(0).getOrDefault("columnComment", tableName));
            }
            table.setTableComment(comment);
            table.setClassName(GenUtils.toClassName(tableName));
            table.setPackageName("com.sprout");
            table.setModuleName("business");
            table.setBusinessName(GenUtils.toCamelCase(tableName));
            table.setFunctionName(comment);
            table.setFunctionAuthor("sprout");
            save(table);

            // 保存字段
            for (Map<String, Object> map : columnMaps) {
                GenTableColumn column = GenUtils.toColumn(map);
                column.setTableId(table.getTableId());
                columnMapper.insert(column);
            }
        }
    }

    @Override
    public IPage<GenTable> pageGenTables(long pageNum, long pageSize, String tableName) {
        return page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<GenTable>()
                        .like(StringUtils.hasText(tableName), GenTable::getTableName, tableName)
                        .orderByDesc(GenTable::getTableId));
    }

    @Override
    public GenTable getGenTable(Long tableId) {
        GenTable table = getById(tableId);
        if (table != null) {
            table.setColumns(columnMapper.selectList(
                    new LambdaQueryWrapper<GenTableColumn>()
                            .eq(GenTableColumn::getTableId, tableId)
                            .orderByAsc(GenTableColumn::getSort)));
        }
        return table;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGenTable(GenTable table) {
        updateById(table);
        if (table.getColumns() != null) {
            for (GenTableColumn column : table.getColumns()) {
                columnMapper.updateById(column);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGenTables(List<Long> tableIds) {
        removeByIds(tableIds);
        columnMapper.delete(new LambdaQueryWrapper<GenTableColumn>().in(GenTableColumn::getTableId, tableIds));
    }

    @Override
    public Map<String, String> previewCode(Long tableId) {
        GenTable table = getGenTable(tableId);
        Map<String, String> result = new HashMap<>();
        for (String template : TEMPLATES) {
            result.put(targetFileName(template, table), render(template, buildContext(table)));
        }
        return result;
    }

    @Override
    public byte[] generateCode(List<Long> tableIds) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(baos)) {
            for (Long tableId : tableIds) {
                GenTable table = getGenTable(tableId);
                for (String template : TEMPLATES) {
                    String content = render(template, buildContext(table));
                    zip.putNextEntry(new ZipEntry(targetFileName(template, table)));
                    zip.write(content.getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
            }
            zip.finish();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("代码生成失败: " + e.getMessage());
        }
    }

    /**
     * 构建模板上下文
     */
    private VelocityContext buildContext(GenTable table) {
        List<GenTableColumn> columns = GenUtils.filterBaseColumns(table.getColumns());
        GenTableColumn pk = columns.stream()
                .filter(c -> "1".equals(c.getIsPk()))
                .findFirst()
                .orElse(columns.isEmpty() ? null : columns.get(0));

        VelocityContext ctx = new VelocityContext();
        ctx.put("packageName", table.getPackageName());
        ctx.put("moduleName", table.getModuleName());
        ctx.put("className", table.getClassName());
        ctx.put("classNameLower", lowerFirst(table.getClassName()));
        ctx.put("businessName", table.getBusinessName());
        ctx.put("functionName", table.getFunctionName());
        ctx.put("author", table.getFunctionAuthor());
        ctx.put("tableName", table.getTableName());
        ctx.put("columns", columns);
        ctx.put("pkColumn", pk);
        ctx.put("pkJavaType", pk == null ? "Long" : pk.getJavaType());
        ctx.put("pkJavaField", pk == null ? "id" : pk.getJavaField());
        return ctx;
    }

    /**
     * 渲染模板
     */
    private String render(String template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        VELOCITY_ENGINE.getTemplate("templates/vm/" + template, StandardCharsets.UTF_8.name())
                .merge(context, writer);
        return writer.toString();
    }

    /**
     * 目标文件名
     */
    private String targetFileName(String template, GenTable table) {
        String path = table.getPackageName().replace('.', '/') + "/" + table.getModuleName() + "/";
        String className = table.getClassName();
        return switch (template) {
            case "entity.java.vm" -> path + "entity/" + className + ".java";
            case "mapper.java.vm" -> path + "mapper/" + className + "Mapper.java";
            case "service.java.vm" -> path + "service/" + className + "Service.java";
            case "serviceImpl.java.vm" -> path + "service/impl/" + className + "ServiceImpl.java";
            case "controller.java.vm" -> path + "controller/" + className + "Controller.java";
            default -> path + className + ".java";
        };
    }

    private String lowerFirst(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
