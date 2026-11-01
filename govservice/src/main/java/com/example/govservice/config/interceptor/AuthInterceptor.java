package com.example.govservice.config.interceptor;

import com.example.govservice.service.TokenBlacklistService;
import com.example.govservice.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {
    
    @Autowired(required = false)
    private TokenBlacklistService tokenBlacklistService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行OPTIONS预检
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        String token = auth.substring("Bearer ".length());
        try {
            // 检查黑名单
            if (tokenBlacklistService != null && tokenBlacklistService.isBlacklisted(token)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            
            Claims claims = JwtUtils.parse(token);
            // 将关键信息放入request，便于Controller获取
            request.setAttribute("jwt.userId", claims.get("userId"));
            request.setAttribute("jwt.role", claims.get("role"));
            request.setAttribute("jwt.language", claims.get("languageType"));
            request.setAttribute("jwt.token", token); // 保存token用于登出
            return true;
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }
}


