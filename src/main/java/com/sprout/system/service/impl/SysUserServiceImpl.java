package com.sprout.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sprout.common.constant.Constants;
import com.sprout.common.exception.BusinessException;
import com.sprout.system.entity.SysUser;
import com.sprout.system.entity.SysUserRole;
import com.sprout.system.mapper.SysUserMapper;
import com.sprout.system.mapper.SysUserRoleMapper;
import com.sprout.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public IPage<SysUser> pageUsers(long pageNum, long pageSize, String username, String status, Long deptId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(username), SysUser::getUsername, username)
                .eq(StringUtils.hasText(status), SysUser::getStatus, status)
                .eq(deptId != null, SysUser::getDeptId, deptId)
                .orderByAsc(SysUser::getUserId);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(SysUser user) {
        checkUsernameUnique(user.getUsername(), null);
        user.setPassword(passwordEncoder.encode(
                StringUtils.hasText(user.getPassword()) ? user.getPassword() : "123456"));
        if (user.getStatus() == null) {
            user.setStatus(Constants.STATUS_NORMAL);
        }
        save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SysUser user) {
        checkUsernameUnique(user.getUsername(), user.getUserId());
        // 不更新密码字段
        user.setPassword(null);
        updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUsers(List<Long> userIds) {
        if (userIds.contains(Constants.SUPER_ADMIN_USER_ID)) {
            throw new BusinessException("超级管理员不允许删除");
        }
        removeByIds(userIds);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userIds));
    }

    @Override
    public void resetPassword(Long userId, String password) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(
                StringUtils.hasText(password) ? password : "123456"));
        updateById(user);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream().map(SysUserRole::getRoleId).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (roleIds != null && !roleIds.isEmpty()) {
            roleIds.forEach(roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);
            });
        }
    }

    private void checkUsernameUnique(String username, Long userId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .ne(userId != null, SysUser::getUserId, userId);
        if (count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
    }
}
