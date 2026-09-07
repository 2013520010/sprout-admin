package com.sprout.system.controller;

import com.sprout.common.result.Result;
import com.sprout.system.dto.LoginBody;
import com.sprout.system.service.AuthService;
import com.sprout.system.vo.LoginVO;
import com.sprout.system.vo.RouterVO;
import com.sprout.system.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 认证接口
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginBody body, HttpServletRequest request) {
        return Result.success(authService.login(body, request));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getInfo() {
        return Result.success(authService.getInfo());
    }

    @Operation(summary = "获取当前用户路由菜单")
    @GetMapping("/routers")
    public Result<List<RouterVO>> getRouters() {
        return Result.success(authService.getRouters());
    }
}
