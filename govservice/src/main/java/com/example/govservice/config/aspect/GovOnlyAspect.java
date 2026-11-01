package com.example.govservice.config.aspect;

import com.example.govservice.config.annotation.GovOnly;
import com.example.govservice.exception.BusinessException;
import com.example.govservice.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 政务权限AOP切面
 */
@Slf4j
@Aspect
@Component
public class GovOnlyAspect {
    
    @Before("@annotation(com.example.govservice.config.annotation.GovOnly) || @within(com.example.govservice.config.annotation.GovOnly)")
    public void checkGovPermission(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        
        HttpServletRequest request = attributes.getRequest();
        Object role = request.getAttribute("jwt.role");
        
        if (!"政务人员".equals(String.valueOf(role))) {
            // 获取注解消息
            String message = "仅限政务人员访问";
            try {
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method method = signature.getMethod();
                GovOnly annotation = method.getAnnotation(GovOnly.class);
                if (annotation != null && annotation.message() != null && !annotation.message().isEmpty()) {
                    message = annotation.message();
                }
            } catch (Exception ignored) {}
            
            log.warn("非政务人员尝试访问政务接口：role={}, method={}", role, joinPoint.getSignature().getName());
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), message);
        }
    }
}

