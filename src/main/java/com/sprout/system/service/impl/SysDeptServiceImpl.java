package com.sprout.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sprout.common.constant.Constants;
import com.sprout.common.exception.BusinessException;
import com.sprout.system.entity.SysDept;
import com.sprout.system.entity.SysUser;
import com.sprout.system.mapper.SysDeptMapper;
import com.sprout.system.mapper.SysUserMapper;
import com.sprout.system.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门服务实现
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    private final SysUserMapper userMapper;

    @Override
    public List<SysDept> selectDeptTree() {
        List<SysDept> all = list(new LambdaQueryWrapper<SysDept>()
                .orderByAsc(SysDept::getParentId, SysDept::getSort));
        return buildTree(all, 0L);
    }

    @Override
    public void createDept(SysDept dept) {
        if (dept.getParentId() == null) {
            dept.setParentId(0L);
        }
        SysDept parent = getById(dept.getParentId());
        dept.setAncestors(parent == null ? "0" : parent.getAncestors() + "," + dept.getParentId());
        if (dept.getStatus() == null) {
            dept.setStatus(Constants.STATUS_NORMAL);
        }
        save(dept);
    }

    @Override
    public void updateDept(SysDept dept) {
        if (dept.getDeptId().equals(dept.getParentId())) {
            throw new BusinessException("上级部门不能选择自己");
        }
        SysDept parent = getById(dept.getParentId());
        dept.setAncestors(parent == null ? "0" : parent.getAncestors() + "," + dept.getParentId());
        updateById(dept);
    }

    @Override
    public void deleteDept(Long deptId) {
        if (count(new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, deptId)) > 0) {
            throw new BusinessException("存在下级部门，不允许删除");
        }
        if (userMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeptId, deptId)) > 0) {
            throw new BusinessException("部门下存在用户，不允许删除");
        }
        removeById(deptId);
    }

    private List<SysDept> buildTree(List<SysDept> depts, Long parentId) {
        return depts.stream()
                .filter(d -> parentId.equals(d.getParentId()))
                .peek(d -> d.setChildren(buildTree(depts, d.getDeptId())))
                .toList();
    }
}
