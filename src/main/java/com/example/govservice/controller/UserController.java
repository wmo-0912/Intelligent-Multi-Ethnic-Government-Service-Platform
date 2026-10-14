package com.example.govservice.controller;

import com.example.govservice.dto.request.UserLoginRequest;
import com.example.govservice.dto.request.UserRegisterRequest;
import com.example.govservice.dto.response.CommonResponse;
import com.example.govservice.dto.response.LoginResponse;
import com.example.govservice.entity.User;
import com.example.govservice.service.PreferenceService;
import com.example.govservice.service.TokenBlacklistService;
import com.example.govservice.service.UserService;
import com.example.govservice.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private PreferenceService preferenceService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public CommonResponse<User> register(@Valid @RequestBody UserRegisterRequest request) {
        log.info("用户注册请求：{}", request.getAccount());
        User user = userService.register(request);
        // 提示：注册完成后建议立即登录获取token
        return CommonResponse.success(user);
    }
    
    /**
     * 用户登录
     */
    @RequestMapping(value = "/login", method = {RequestMethod.POST, RequestMethod.GET})
    public CommonResponse<LoginResponse> login(@RequestParam(required = false) String account,
                                               @RequestParam(required = false) String password,
                                               @RequestBody(required = false) java.util.Map<String, Object> body) {
        if ((account == null || password == null) && body != null) {
            Object a = body.get("account");
            Object p = body.get("password");
            account = account == null && a != null ? String.valueOf(a) : account;
            password = password == null && p != null ? String.valueOf(p) : password;
        }
        UserLoginRequest request = new UserLoginRequest(account, password);
        log.info("用户登录请求：{}", request.getAccount());
        User user = userService.login(request);

        String lang = preferenceService.getUserLanguage(user.getUserId());
        if (lang == null) lang = "zh-CN";

        java.util.Map<String, Object> i18nConfig = new java.util.HashMap<>();
        // RTL语言：维吾尔语(ug/ug-CN)、哈萨克语(kk)
        i18nConfig.put("direction", (lang.startsWith("ug") || lang.startsWith("kk")) ? "rtl" : "ltr");
        // 竖排文字：藏语(bo/bo-CN)
        i18nConfig.put("verticalWriting", lang.startsWith("bo"));
        i18nConfig.put("realNameVerified", user.getRealNameVerified());

        LoginResponse resp = new LoginResponse(
                user.getUserId(),
                user.getName(),
                user.getRole(),
                user.getRole() != null && user.getRole().contains("政务")
                        ? java.util.Arrays.asList("ITEM_REVIEW", "ITEM_MANAGE", "USER_MANAGE")
                        : java.util.Arrays.asList("ITEM_QUERY", "ITEM_APPLY"),
                lang,
                i18nConfig
        );

        java.util.Map<String,Object> claims = new java.util.HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("role", user.getRole());
        claims.put("languageType", lang);
        String token = JwtUtils.generateToken(String.valueOf(user.getUserId()), claims);
        resp.getI18nConfig().put("token", token);
        return CommonResponse.success(resp);
    }

    /**
     * 获取当前登录用户信息（从token）
     */
    @GetMapping("/profile")
    public CommonResponse<User> profile(javax.servlet.http.HttpServletRequest request) {
        Object id = request.getAttribute("jwt.userId");
        Integer userId = id == null ? null : Integer.valueOf(String.valueOf(id));
        User user = userService.getUserById(userId);
        return CommonResponse.success(user);
    }
    
    /**
     * 根据用户ID查询用户信息
     */
    @GetMapping("/{userId}")
    public CommonResponse<User> getUserById(@PathVariable Integer userId) {
        log.info("查询用户信息，用户ID：{}", userId);
        User user = userService.getUserById(userId);
        return CommonResponse.success(user);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    public CommonResponse<User> updateUser(@PathVariable Integer userId, 
                                         @Valid @RequestBody User user) {
        log.info("更新用户信息，用户ID：{}", userId);
        user.setUserId(userId);
        User updatedUser = userService.updateUser(user);
        return CommonResponse.success(updatedUser);
    }

    /**
     * 刷新Token（延长有效期）
     */
    @PostMapping("/refresh")
    public CommonResponse<java.util.Map<String, String>> refreshToken(javax.servlet.http.HttpServletRequest request) {
        Object id = request.getAttribute("jwt.userId");
        Object role = request.getAttribute("jwt.role");
        Object lang = request.getAttribute("jwt.language");
        
        Integer userId = id == null ? null : Integer.valueOf(String.valueOf(id));
        String userRole = role == null ? "" : String.valueOf(role);
        String languageType = lang == null ? "zh-CN" : String.valueOf(lang);
        
        // 生成新token
        java.util.Map<String,Object> claims = new java.util.HashMap<>();
        claims.put("userId", userId);
        claims.put("role", userRole);
        claims.put("languageType", languageType);
        String newToken = JwtUtils.generateToken(String.valueOf(userId), claims);
        
        // 将旧token加入黑名单
        Object oldToken = request.getAttribute("jwt.token");
        if (oldToken != null) {
            tokenBlacklistService.addToBlacklist(String.valueOf(oldToken));
        }
        
        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("token", newToken);
        return CommonResponse.success(result);
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public CommonResponse<Void> logout(javax.servlet.http.HttpServletRequest request) {
        Object token = request.getAttribute("jwt.token");
        if (token != null) {
            tokenBlacklistService.addToBlacklist(String.valueOf(token));
            log.info("用户已登出，token已加入黑名单");
        }
        return CommonResponse.success();
    }
    
    /**
     * 设置用户语言偏好（临时内存）
     */
    @PutMapping("/{userId}/language")
    public CommonResponse<Void> setLanguage(@PathVariable Integer userId,
                                            @RequestParam String languageType) {
        preferenceService.setUserLanguage(userId, languageType);
        return CommonResponse.success();
    }

    /**
     * 使用token设置当前用户语言偏好
     */
    @PutMapping("/me/language")
    public CommonResponse<Void> setMyLanguage(javax.servlet.http.HttpServletRequest request,
                                              @RequestParam String languageType) {
        Object id = request.getAttribute("jwt.userId");
        Integer userId = id == null ? null : Integer.valueOf(String.valueOf(id));
        preferenceService.setUserLanguage(userId, languageType);
        return CommonResponse.success();
    }
    
    /**
     * 查询所有用户（政务人员功能）
     */
    @GetMapping("/all")
    public CommonResponse<List<User>> getAllUsers() {
        log.info("查询所有用户");
        List<User> users = userService.getAllUsers();
        return CommonResponse.success(users);
    }
    
    /**
     * 根据角色查询用户（政务人员功能）
     */
    @GetMapping("/role/{role}")
    public CommonResponse<List<User>> getUsersByRole(@PathVariable String role) {
        log.info("根据角色查询用户，角色：{}", role);
        List<User> users = userService.getUsersByRole(role);
        return CommonResponse.success(users);
    }
    
    /**
     * 删除用户（政务人员功能）
     */
    @DeleteMapping("/{userId}")
    public CommonResponse<Void> deleteUser(@PathVariable Integer userId) {
        log.info("删除用户，用户ID：{}", userId);
        userService.deleteUser(userId);
        return CommonResponse.success();
    }

    /**
     * 冻结/解冻账号（政务人员功能）
     */
    @PutMapping("/{userId}/freeze")
    public CommonResponse<Void> freezeUser(@PathVariable Integer userId,
                                           @RequestParam boolean frozen) {
        User u = userService.getUserById(userId);
        u.setFrozen(frozen);
        userService.updateUser(u);
        return CommonResponse.success();
    }
    
    /**
     * 检查账号是否存在
     */
    @GetMapping("/check-account/{account}")
    public CommonResponse<Boolean> checkAccountExists(@PathVariable String account) {
        log.info("检查账号是否存在：{}", account);
        boolean exists = userService.checkAccountExists(account);
        return CommonResponse.success(exists);
    }
    
    /**
     * 检查手机号是否存在
     */
    @GetMapping("/check-phone/{phoneNumber}")
    public CommonResponse<Boolean> checkPhoneNumberExists(@PathVariable String phoneNumber) {
        log.info("检查手机号是否存在：{}", phoneNumber);
        boolean exists = userService.checkPhoneNumberExists(phoneNumber);
        return CommonResponse.success(exists);
    }
}
