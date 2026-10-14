package com.example.govservice.dto;

import com.example.govservice.dto.response.CommonResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通用响应对象测试
 */
class CommonResponseTest {

    @Test
    void testSuccessWithData() {
        String data = "test data";
        CommonResponse<String> response = CommonResponse.success(data);

        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("操作成功", response.getMessage());
        assertEquals("test data", response.getData());
    }

    @Test
    void testSuccessWithoutData() {
        CommonResponse<Void> response = CommonResponse.success();

        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("操作成功", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testError() {
        CommonResponse<Void> response = CommonResponse.error(500, "系统错误");

        assertNotNull(response);
        assertEquals(500, response.getCode());
        assertEquals("系统错误", response.getMessage());
    }

    @Test
    void testErrorWithCodeAndMessage() {
        CommonResponse<Void> response = CommonResponse.error(1001, "参数错误");

        assertNotNull(response);
        assertEquals(1001, response.getCode());
        assertEquals("参数错误", response.getMessage());
    }
}

