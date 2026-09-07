package com.sprout.log.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprout.common.utils.IpUtils;
import com.sprout.log.annotation.Log;
import com.sprout.log.entity.SysOperLog;
import com.sprout.log.mapper.SysOperLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 操作日志切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysOperLogMapper operLogMapper;
    private final ObjectMapper objectMapper;

    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        long start = System.currentTimeMillis();
        SysOperLog operLog = new SysOperLog();
        try {
            Object result = joinPoint.proceed();
            operLog.setStatus(0);
            // 结果太长时截断
            String json = objectMapper.writeValueAsString(result);
            operLog.setJsonResult(json.length() > 2000 ? json.substring(0, 2000) : json);
            return result;
        } catch (Throwable e) {
            operLog.setStatus(1);
            operLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            operLog.setCostTime(System.currentTimeMillis() - start);
            fillLog(joinPoint, log, operLog);
            operLogMapper.insert(operLog);
        }
    }

    private void fillLog(ProceedingJoinPoint joinPoint, Log log, SysOperLog operLog) {
        operLog.setTitle(log.title());
        operLog.setBusinessType(log.businessType());
        operLog.setOperTime(LocalDateTime.now());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        operLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName() + "()");

        // 操作人
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null
                && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
            operLog.setOperName(authentication.getName());
        } else {
            operLog.setOperName("anonymous");
        }

        // 请求信息
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            operLog.setRequestMethod(request.getMethod());
            operLog.setOperUrl(request.getRequestURI());
            operLog.setOperIp(IpUtils.getClientIp(request));
        }

        // 请求参数（过滤掉文件、HttpServletRequest/Response 等无法序列化的对象）
        String params = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> !(arg instanceof MultipartFile)
                        && !(arg instanceof HttpServletRequest)
                        && !(arg instanceof jakarta.servlet.http.HttpServletResponse))
                .map(arg -> {
                    try {
                        return objectMapper.writeValueAsString(arg);
                    } catch (Exception e) {
                        return Objects.toString(arg);
                    }
                })
                .collect(Collectors.joining(", "));
        operLog.setOperParam(params.length() > 2000 ? params.substring(0, 2000) : params);
    }
}
