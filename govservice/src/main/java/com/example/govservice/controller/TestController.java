package com.example.govservice.controller;

import com.example.govservice.dto.response.CommonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    /**
     * 简单测试接口
     */
    @GetMapping("/hello")
    public CommonResponse<String> hello() {
        return CommonResponse.success("Hello, 政务服务平台启动成功！");
    }
    
    /**
     * 系统状态测试
     */
    @GetMapping("/status")
    public CommonResponse<Map<String, Object>> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "运行正常");
        status.put("timestamp", LocalDateTime.now());
        status.put("message", "政务服务平台后端系统运行正常");
        status.put("version", "1.0.0");
        
        return CommonResponse.success(status);
    }
}
