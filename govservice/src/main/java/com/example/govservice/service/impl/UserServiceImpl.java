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
    
    /**
     * 政务人员注册码（应存储在配置文件中或数据库中，这里暂时使用常量）
     * 生产环境应使用更安全的方式管理，如存储在数据库、配置文件或环境变量中
     */
    private static final String VALID_REGISTRATION_CODES = "GOV2024,GOV2025,ADMIN2024";
    
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
        
        // 设置角色：如果请求中有role且有效，使用请求的role；否则默认为"居民"
        String requestedRole = request.getRole();
        String finalRole = "居民"; // 默认角色
        
        if (requestedRole != null && !requestedRole.trim().isEmpty()) {
            // 验证role是否合法（只能是"居民"或"政务人员"）
            if ("政务人员".equals(requestedRole)) {
                // 政务人员需要验证注册码
                String registrationCode = request.getRegistrationCode();
                if (registrationCode == null || registrationCode.trim().isEmpty()) {
                    log.warn("政务人员注册时缺少注册码，账号：{}", request.getAccount());
                    throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "政务人员注册需要提供有效的注册码");
                }
                
                // 验证注册码
                if (!isValidRegistrationCode(registrationCode.trim())) {
                    log.warn("政务人员注册时使用了无效的注册码，账号：{}，注册码：{}", request.getAccount(), registrationCode);
                    throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "注册码无效，无法注册为政务人员");
                }
                
                finalRole = "政务人员";
                log.info("政务人员注册验证通过，账号：{}", request.getAccount());
            } else if ("居民".equals(requestedRole)) {
                finalRole = "居民";
            } else {
                // 如果role不合法，使用默认值
                log.warn("用户注册时指定了无效的角色：{}，将使用默认角色'居民'", requestedRole);
            }
        }
        
        // 创建用户对象
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setRole(finalRole);
        
        // 密码加密（BCrypt）
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(request.getPassword()));
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

        log.info("用户注册成功，用户ID：{}，角色：{}，实名认证：{}", user.getUserId(), user.getRole(), user.getRealNameVerified());
        return user;
    }
    
    /**
     * 验证政务人员注册码是否有效
     * @param code 注册码
     * @return 是否有效
     */
    private boolean isValidRegistrationCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        
        // 从配置的注册码列表中验证
        // 实际项目中应该从数据库或配置文件读取
        String[] validCodes = VALID_REGISTRATION_CODES.split(",");
        for (String validCode : validCodes) {
            if (validCode.trim().equalsIgnoreCase(code.trim())) {
                return true;
            }
        }
        
        return false;
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
        if (userId == null) {
            log.error("getUserById: userId为空");
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "用户ID不能为空");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.error("getUserById: 用户不存在，userId={}", userId);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        log.debug("getUserById成功: userId={}, name={}", userId, user.getName());
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
