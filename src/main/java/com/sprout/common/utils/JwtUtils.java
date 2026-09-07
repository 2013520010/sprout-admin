package com.sprout.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具类（基于 jjwt 0.12.x 新 API）
 */
@Component
public class JwtUtils {

    @Value("${sprout.jwt.secret}")
    private String secret;

    @Value("${sprout.jwt.expire-minutes}")
    private long expireMinutes;

    /**
     * 生成密钥
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建令牌
     *
     * @param subject 主题（这里存放令牌唯一 uuid）
     * @return 令牌
     */
    public String createToken(String subject) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + expireMinutes * 60 * 1000);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(subject)
                .issuedAt(now)
                .expiration(expire)
                .signWith(getKey())
                .compact();
    }

    /**
     * 解析令牌，获取 Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取令牌主题（uuid）
     */
    public String getSubject(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 校验令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取令牌剩余有效期（毫秒）
     */
    public long getRemainingTime(String token) {
        return parseToken(token).getExpiration().getTime() - System.currentTimeMillis();
    }
}
