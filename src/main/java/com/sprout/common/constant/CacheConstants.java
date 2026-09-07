package com.sprout.common.constant;

/**
 * 缓存 Key 常量
 */
public final class CacheConstants {

    private CacheConstants() {
    }

    /** 登录令牌前缀 */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /** 验证码前缀 */
    public static final String CAPTCHA_CODE_KEY = "captcha:";

    /** 用户权限缓存前缀 */
    public static final String USER_PERMS_KEY = "user_perms:";

    /** 参数配置前缀 */
    public static final String CONFIG_KEY = "sys_config:";

    /** 令牌有效期（分钟）兜底值 */
    public static final long DEFAULT_EXPIRE_MINUTES = 120;
}
