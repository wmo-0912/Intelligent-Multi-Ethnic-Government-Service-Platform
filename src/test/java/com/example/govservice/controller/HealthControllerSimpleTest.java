package com.example.govservice.controller;

import com.example.govservice.dto.response.CommonResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 健康检查控制器简单测试
 */
class HealthControllerSimpleTest {

    @Test
    void testCommonResponseStructure() {
        // 测试 CommonResponse 的基本结构
        CommonResponse<Map<String, Object>> response = new CommonResponse<>();
        response.setCode(200);
        response.setMessage("操作成功");
        
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("service", "政务服务平台");
        data.put("version", "1.0.0");
        response.setData(data);
        
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("操作成功", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("UP", response.getData().get("status"));
    }

    @Test
    void testStaticFactoryMethods() {
        // 测试静态工厂方法
        CommonResponse<String> response1 = CommonResponse.success("测试数据");
        assertEquals(200, response1.getCode());
        assertEquals("操作成功", response1.getMessage());
        assertEquals("测试数据", response1.getData());
        
        CommonResponse<Void> response2 = CommonResponse.success();
        assertEquals(200, response2.getCode());
        assertNull(response2.getData());
    }
}

