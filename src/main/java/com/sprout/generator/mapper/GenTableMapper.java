package com.sprout.generator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sprout.generator.entity.GenTable;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 代码生成表 Mapper
 */
public interface GenTableMapper extends BaseMapper<GenTable> {

    /**
     * 查询数据库表列表（排除系统表、生成器表）
     */
    @Select("SELECT table_name AS tableName, table_comment AS tableComment, create_time AS createTime, " +
            "update_time AS updateTime FROM information_schema.tables " +
            "WHERE table_schema = (SELECT DATABASE()) " +
            "AND table_name NOT LIKE 'sys_%' AND table_name NOT LIKE 'gen_%' " +
            "ORDER BY create_time DESC")
    List<Map<String, Object>> selectDbTableList();

    /**
     * 查询指定表是否存在
     */
    @Select("SELECT COUNT(1) FROM information_schema.tables " +
            "WHERE table_schema = (SELECT DATABASE()) AND table_name = #{tableName}")
    int countDbTable(@Param("tableName") String tableName);

    /**
     * 查询表字段信息
     */
    @Select("SELECT column_name AS columnName, " +
            "IFNULL(column_comment, column_name) AS columnComment, " +
            "column_type AS columnType, data_type AS dataType, " +
            "column_key AS columnKey, extra AS extra, " +
            "is_nullable AS isNullable, ordinal_position AS ordinalPosition " +
            "FROM information_schema.columns " +
            "WHERE table_schema = (SELECT DATABASE()) AND table_name = #{tableName} " +
            "ORDER BY ordinal_position")
    List<Map<String, Object>> selectDbTableColumns(@Param("tableName") String tableName);
}
