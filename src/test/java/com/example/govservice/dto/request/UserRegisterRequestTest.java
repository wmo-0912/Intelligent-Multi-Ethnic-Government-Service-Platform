package com.example.govservice.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户注册请求测试
 */
class UserRegisterRequestTest {

    @Test
    void testUserRegisterRequest() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("张三");
        request.setAccount("zhangsan");
        request.setPassword("123456");
        request.setPhoneNumber("13800000000");
        request.setEthnicity("汉族");

        assertNotNull(request);
        assertEquals("张三", request.getName());
        assertEquals("zhangsan", request.getAccount());
        assertEquals("123456", request.getPassword());
        assertEquals("13800000000", request.getPhoneNumber());
        assertEquals("汉族", request.getEthnicity());
    }

    @Test
    void testUserLoginRequest() {
        UserLoginRequest request = new UserLoginRequest();
        request.setAccount("zhangsan");
        request.setPassword("123456");

        assertNotNull(request);
        assertEquals("zhangsan", request.getAccount());
        assertEquals("123456", request.getPassword());
    }
}

