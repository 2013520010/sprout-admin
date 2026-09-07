package com.sprout.system.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录结果
 */
@Data
@Builder
public class LoginVO {

    /** 令牌 */
    private String token;

    /** 令牌类型 */
    private String tokenType;

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;
}
