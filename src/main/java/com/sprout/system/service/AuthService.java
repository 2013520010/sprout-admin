package com.sprout.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sprout.common.constant.CacheConstants;
import com.sprout.common.constant.Constants;
import com.sprout.common.exception.BusinessException;
import com.sprout.common.result.ResultCode;
import com.sprout.common.utils.IpUtils;
import com.sprout.common.utils.JwtUtils;
import com.sprout.common.utils.RedisUtils;
import com.sprout.common.utils.SecurityUtils;
import com.sprout.log.entity.SysLoginLog;
import com.sprout.log.mapper.SysLoginLogMapper;
import com.sprout.security.LoginUser;
import com.sprout.system.dto.LoginBody;
import com.sprout.system.entity.SysMenu;
import com.sprout.system.entity.SysUser;
import com.sprout.system.mapper.SysMenuMapper;
import com.sprout.system.mapper.SysUserMapper;
import com.sprout.system.vo.LoginVO;
import com.sprout.system.vo.RouterVO;
import com.sprout.system.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final SysMenuMapper menuMapper;
    private final SysLoginLogMapper loginLogMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;

    /**
     * 登录
     */
    public LoginVO login(LoginBody body, HttpServletRequest request) {
        String username = body.getUsername();
        String ip = IpUtils.getClientIp(request);

        // 查询用户
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            recordLoginLog(username, Constants.STATUS_DISABLE, "用户不存在", ip);
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
        // 校验密码
        if (!passwordEncoder.matches(body.getPassword(), user.getPassword())) {
            recordLoginLog(username, Constants.STATUS_DISABLE, "密码错误", ip);
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
        // 校验状态
        if (Constants.STATUS_DISABLE.equals(user.getStatus())) {
            recordLoginLog(username, Constants.STATUS_DISABLE, "账号已停用", ip);
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被停用，请联系管理员");
        }

        // 构建登录用户（角色 + 权限）
        LoginUser loginUser = buildLoginUser(user);

        // 生成令牌并缓存
        String uuid = UUID.randomUUID().toString();
        String token = jwtUtils.createToken(uuid);
        redisUtils.set(CacheConstants.LOGIN_TOKEN_KEY + uuid, loginUser,
                CacheConstants.DEFAULT_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 更新最后登录信息
        user.setLoginIp(ip);
        user.setLoginDate(LocalDateTime.now());
        userMapper.updateById(user);

        recordLoginLog(username, Constants.STATUS_NORMAL, "登录成功", ip);

        return LoginVO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }

    /**
     * 登出
     */
    public void logout(HttpServletRequest request) {
        String uuid = resolveUuid(request);
        if (uuid != null) {
            redisUtils.delete(CacheConstants.LOGIN_TOKEN_KEY + uuid);
        }
    }

    /**
     * 获取当前用户信息
     */
    public UserInfoVO getInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = userMapper.selectById(loginUser.getUserId());
        return UserInfoVO.builder()
                .user(user)
                .roles(loginUser.getRoles())
                .permissions(loginUser.getPermissions())
                .build();
    }

    /**
     * 获取当前用户菜单（构建路由树）
     */
    public List<RouterVO> getRouters() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<SysMenu> menus;
        if (loginUser.getRoles().contains(Constants.SUPER_ADMIN_ROLE)) {
            menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                    .in(SysMenu::getMenuType, Constants.MENU_TYPE_DIR, Constants.MENU_TYPE_MENU)
                    .eq(SysMenu::getStatus, Constants.STATUS_NORMAL)
                    .orderByAsc(SysMenu::getParentId, SysMenu::getSort));
        } else {
            menus = menuMapper.selectMenusByUserId(loginUser.getUserId());
        }
        return buildTree(menus, 0L);
    }

    /**
     * 构建登录用户（含角色、权限）
     */
    private LoginUser buildLoginUser(SysUser user) {
        Set<String> roles = new HashSet<>(userMapper.selectRoleKeysByUserId(user.getUserId()));
        Set<String> permissions;
        if (roles.contains(Constants.SUPER_ADMIN_ROLE)) {
            permissions = new HashSet<>(menuMapper.selectAllPerms());
        } else {
            permissions = new HashSet<>(userMapper.selectMenuPermsByUserId(user.getUserId()));
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getNickname());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setRoles(roles);
        loginUser.setPermissions(permissions);
        loginUser.setLoginTime(System.currentTimeMillis());
        return loginUser;
    }

    /**
     * 从请求解析令牌 uuid
     */
    private String resolveUuid(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtils.validateToken(token)) {
                return jwtUtils.getSubject(token);
            }
        }
        return null;
    }

    /**
     * 构建菜单树
     */
    private List<RouterVO> buildTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .map(m -> {
                    RouterVO vo = new RouterVO();
                    vo.setId(m.getMenuId());
                    vo.setParentId(m.getParentId());
                    vo.setName(m.getMenuName());
                    vo.setPath(m.getPath());
                    vo.setComponent(m.getComponent());
                    vo.setIcon(m.getIcon());
                    List<RouterVO> children = buildTree(menus, m.getMenuId());
                    vo.setChildren(children.isEmpty() ? null : children);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(String username, String status, String msg, String ip) {
        SysLoginLog log = new SysLoginLog();
        log.setUsername(username);
        log.setIp(ip);
        log.setStatus(status);
        log.setMsg(msg);
        log.setLoginTime(LocalDateTime.now());
        loginLogMapper.insert(log);
    }
}
