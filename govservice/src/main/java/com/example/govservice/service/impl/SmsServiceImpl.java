package com.example.govservice.service.impl;

import com.example.govservice.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短信服务实现（占位）
 * TODO: 对接实际短信服务商（如阿里云、腾讯云）
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {
    
    @Override
    public boolean sendSms(String phoneNumber, String content) {
        // 占位实现：仅记录日志
        log.info("[短信服务占位] 发送短信到：{}，内容：{}", phoneNumber, content);
        
        // TODO: 实际对接短信服务商API
        // 示例：
        // try {
        //     SendSmsResponse response = smsClient.sendSms(
        //         new SendSmsRequest()
        //             .setPhoneNumbers(phoneNumber)
        //             .setSignName("政务服务平台")
        //             .setTemplateCode("SMS_123456")
        //             .setTemplateParam("{\"content\":\"" + content + "\"}")
        //     );
        //     return "OK".equals(response.getCode());
        // } catch (Exception e) {
        //     log.error("短信发送失败", e);
        //     return false;
        // }
        
        return true;
    }
    
    @Override
    public boolean sendVerificationCode(String phoneNumber, String code) {
        // 占位实现：仅记录日志
        log.info("[短信服务占位] 发送验证码到：{}，验证码：{}", phoneNumber, code);
        
        // TODO: 实际对接短信服务商API
        return true;
    }
}

