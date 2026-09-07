package com.sprout.security;

import com.sprout.common.constant.CacheConstants;
import com.sprout.common.utils.JwtUtils;
import com.sprout.common.utils.RedisUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT 认证过滤器：解析令牌 -> 从 Redis 加载登录用户 -> 构建认证信息
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;

    @Value("${sprout.jwt.header}")
    private String header;

    @Value("${sprout.jwt.prefix}")
    private String prefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        // 令牌存在且当前未认证
        if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwtUtils.parseToken(token);
                String uuid = claims.getSubject();
                // 从 Redis 加载登录用户
                LoginUser loginUser = redisUtils.get(CacheConstants.LOGIN_TOKEN_KEY + uuid);
                if (loginUser != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(loginUser, null, buildAuthorities(loginUser));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ignored) {
                // 令牌无效则保持未认证状态，由 EntryPoint 返回 401
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 构建权限集合：权限标识 + 角色（ROLE_ 前缀）
     */
    private List<SimpleGrantedAuthority> buildAuthorities(LoginUser loginUser) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (loginUser.getPermissions() != null) {
            loginUser.getPermissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
        }
        if (loginUser.getRoles() != null) {
            loginUser.getRoles().forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
        }
        return authorities;
    }

    /**
     * 从请求头解析令牌
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(header);
        if (StringUtils.hasText(bearer) && bearer.startsWith(prefix)) {
            return bearer.substring(prefix.length());
        }
        return null;
    }
}
