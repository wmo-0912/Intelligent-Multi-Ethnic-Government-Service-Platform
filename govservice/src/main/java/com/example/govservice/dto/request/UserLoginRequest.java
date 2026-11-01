package com.example.govservice.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    
    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String account;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
