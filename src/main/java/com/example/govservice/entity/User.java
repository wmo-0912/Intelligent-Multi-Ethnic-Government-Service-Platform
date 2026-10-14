package com.example.govservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表：User
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 民族
     */
    private String ethnicity;
    
    /**
     * 联系方式
     */
    private String phoneNumber;
    
    /**
     * 账号
     */
    private String account;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 角色：居民/政务人员
     */
    private String role;
    
    /**
     * 实名认证状态
     */
    private Boolean realNameVerified;

    /**
     * 账号是否冻结
     */
    private Boolean frozen;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
