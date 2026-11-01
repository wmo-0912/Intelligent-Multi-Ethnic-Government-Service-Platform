package com.example.govservice.service;

/**
 * 短信服务接口（占位，待对接实际短信网关）
 */
public interface SmsService {
    
    /**
     * 发送短信
     * @param phoneNumber 手机号
     * @param content 短信内容
     * @return 是否发送成功
     */
    boolean sendSms(String phoneNumber, String content);
    
    /**
     * 发送验证码短信
     * @param phoneNumber 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phoneNumber, String code);
}

