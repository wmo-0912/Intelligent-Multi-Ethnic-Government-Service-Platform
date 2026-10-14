package com.example.govservice.service;

import com.example.govservice.dto.request.UserLoginRequest;
import com.example.govservice.dto.request.UserRegisterRequest;
import com.example.govservice.entity.User;
import com.example.govservice.mapper.UserMapper;
import com.example.govservice.service.impl.UserServiceImpl;
import com.example.govservice.util.EncryptUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRegisterRequest registerRequest;
    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("测试用户");
        testUser.setAccount("testuser");
        testUser.setPassword(EncryptUtils.md5("123456"));
        testUser.setPhoneNumber("13800000000");
        testUser.setRole("居民");
        testUser.setEthnicity("汉族");

        registerRequest = new UserRegisterRequest();
        registerRequest.setName("新用户");
        registerRequest.setAccount("newuser");
        registerRequest.setPassword("123456");
        registerRequest.setPhoneNumber("13900000000");
        registerRequest.setEthnicity("汉族");

        loginRequest = new UserLoginRequest();
        loginRequest.setAccount("testuser");
        loginRequest.setPassword("123456");
    }

    /**
     * 测试检查账号是否存在
     */
    @Test
    void testCheckAccountExists() {
        when(userMapper.checkAccountExists("testuser")).thenReturn(1);

        boolean result = userService.checkAccountExists("testuser");

        assertTrue(result);
    }

    /**
     * 测试检查手机号是否存在
     */
    @Test
    void testCheckPhoneNumberExists() {
        when(userMapper.checkPhoneNumberExists("13800000000")).thenReturn(1);

        boolean result = userService.checkPhoneNumberExists("13800000000");

        assertTrue(result);
    }
}
