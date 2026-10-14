package com.example.govservice.service.impl;

import com.example.govservice.dto.request.UserLoginRequest;
import com.example.govservice.dto.request.UserRegisterRequest;
import com.example.govservice.entity.User;
import com.example.govservice.exception.BusinessException;
import com.example.govservice.exception.ErrorCode;
import com.example.govservice.mapper.UserMapper;
import com.example.govservice.service.RealNameService;
import com.example.govservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RealNameService realNameService;
    
    @Override
    public User register(UserRegisterRequest request) {
        // 检查账号是否已存在
        if (checkAccountExists(request.getAccount())) {
            throw new BusinessException(ErrorCode.USER_ACCOUNT_EXISTS);
        }
        
        // 检查手机号是否已存在
        if (checkPhoneNumberExists(request.getPhoneNumber())) {
            throw new BusinessException(ErrorCode.USER_PHONE_EXISTS);
        }
        
        // 创建用户对象
        User user = new User();
        BeanUtils.copyProperties(request, user);
        
        // 密码加密（BCrypt）
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(request.getPassword()));
        
        // 设置默认角色为居民
        user.setRole("居民");
        user.setRealNameVerified(false);
        
        // 设置创建时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 插入数据库
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "用户注册失败");
        }
        
        // 实名认证占位调用
        boolean verified = realNameService.verifyUser(user);
        if (verified) {
            user.setRealNameVerified(true);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.update(user);
        }

        log.info("用户注册成功，用户ID：{}，实名认证：{}", user.getUserId(), user.getRealNameVerified());
        return user;
    }
    
    @Override
    public User login(UserLoginRequest request) {
        // 根据账号查询用户
        User user = userMapper.selectByAccount(request.getAccount());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_LOGIN_FAILED);
        }
        
        // 验证密码（BCrypt）
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USER_LOGIN_FAILED);
        }
        
        log.info("用户登录成功，用户ID：{}", user.getUserId());
        return user;
    }
    
    @Override
    public User getUserById(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }
    
    @Override
    public User updateUser(User user) {
        // 检查用户是否存在
        User existingUser = userMapper.selectById(user.getUserId());
        if (existingUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        
        // 更新数据库
        int result = userMapper.update(user);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "用户信息更新失败");
        }
        
        log.info("用户信息更新成功，用户ID：{}", user.getUserId());
        return user;
    }
    
    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }
    
    @Override
    public List<User> getUsersByRole(String role) {
        return userMapper.selectByRole(role);
    }
    
    @Override
    public boolean deleteUser(Integer userId) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        int result = userMapper.deleteById(userId);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "用户删除失败");
        }
        
        log.info("用户删除成功，用户ID：{}", userId);
        return true;
    }
    
    @Override
    public boolean checkAccountExists(String account) {
        return userMapper.checkAccountExists(account) > 0;
    }
    
    @Override
    public boolean checkPhoneNumberExists(String phoneNumber) {
        return userMapper.checkPhoneNumberExists(phoneNumber) > 0;
    }
}
