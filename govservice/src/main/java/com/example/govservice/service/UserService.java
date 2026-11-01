package com.example.govservice.service;

import com.example.govservice.dto.request.UserLoginRequest;
import com.example.govservice.dto.request.UserRegisterRequest;
import com.example.govservice.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    User register(UserRegisterRequest request);
    
    /**
     * 用户登录
     */
    User login(UserLoginRequest request);
    
    /**
     * 根据用户ID查询用户信息
     */
    User getUserById(Integer userId);
    
    /**
     * 更新用户信息
     */
    User updateUser(User user);
    
    /**
     * 查询所有用户
     */
    List<User> getAllUsers();
    
    /**
     * 根据角色查询用户
     */
    List<User> getUsersByRole(String role);
    
    /**
     * 删除用户
     */
    boolean deleteUser(Integer userId);
    
    /**
     * 检查账号是否存在
     */
    boolean checkAccountExists(String account);
    
    /**
     * 检查手机号是否存在
     */
    boolean checkPhoneNumberExists(String phoneNumber);
}
