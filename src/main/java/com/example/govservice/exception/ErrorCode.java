package com.example.govservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // 通用错误码
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(1001, "参数错误"),
    INVALID_PARAM(400, "无效的参数"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    RESOURCE_NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统内部错误"),
    
    // 用户相关错误码
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_ALREADY_EXISTS(2002, "用户已存在"),
    USER_ACCOUNT_EXISTS(2003, "账号已存在"),
    USER_PHONE_EXISTS(2004, "手机号已存在"),
    USER_LOGIN_FAILED(2005, "登录失败，账号或密码错误"),
    USER_PERMISSION_DENIED(2006, "权限不足"),
    
    // 事项相关错误码
    SERVICE_ITEM_NOT_FOUND(3001, "事项不存在"),
    SERVICE_ITEM_DISABLED(3002, "事项已禁用"),
    
    // 申请相关错误码
    APPLICATION_NOT_FOUND(4001, "申请记录不存在"),
    APPLICATION_STATUS_ERROR(4002, "申请状态错误"),
    
    // 多语言相关错误码
    LANGUAGE_NOT_SUPPORTED(5001, "不支持的语言类型"),
    TRANSLATION_NOT_FOUND(5002, "翻译信息不存在"),
    
    // FAQ相关错误码
    FAQ_NOT_FOUND(6001, "FAQ不存在");
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 错误消息
     */
    private final String message;
}
