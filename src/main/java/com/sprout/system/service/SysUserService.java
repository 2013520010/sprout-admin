package com.sprout.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sprout.system.entity.SysUser;

import java.util.List;

/**
 * 用户服务
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询用户
     */
    IPage<SysUser> pageUsers(long pageNum, long pageSize, String username, String status, Long deptId);

    /**
     * 新增用户
     */
    void createUser(SysUser user);

    /**
     * 修改用户
     */
    void updateUser(SysUser user);

    /**
     * 批量删除用户
     */
    void deleteUsers(List<Long> userIds);

    /**
     * 重置密码
     */
    void resetPassword(Long userId, String password);

    /**
     * 查询用户已有角色 ID
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 分配角色
     */
    void assignRoles(Long userId, List<Long> roleIds);
}
