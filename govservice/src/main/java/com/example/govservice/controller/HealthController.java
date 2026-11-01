package com.example.govservice.controller;

import com.example.govservice.dto.response.CommonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统健康检查控制器
 */
@RestController
@RequestMapping("/health")
public class HealthController {
    
    /**
     * 系统健康检查
     */
    @GetMapping("/check")
    public CommonResponse<Map<String, Object>> healthCheck() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("service", "政务服务平台");
        healthInfo.put("version", "1.0.0");
        healthInfo.put("description", "政务服务平台后端系统运行正常");
        
        return CommonResponse.success(healthInfo);
    }
    
    /**
     * 系统信息
     */
    @GetMapping("/info")
    public CommonResponse<Map<String, Object>> systemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        systemInfo.put("userTimezone", System.getProperty("user.timezone"));
        systemInfo.put("startupTime", LocalDateTime.now());
        
        return CommonResponse.success(systemInfo);
    }
}
