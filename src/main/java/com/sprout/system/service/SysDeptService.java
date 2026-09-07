package com.sprout.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sprout.system.entity.SysDept;

import java.util.List;

/**
 * 部门服务
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 查询部门树
     */
    List<SysDept> selectDeptTree();

    void createDept(SysDept dept);

    void updateDept(SysDept dept);

    void deleteDept(Long deptId);
}
