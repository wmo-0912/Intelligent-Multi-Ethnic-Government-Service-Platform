package com.example.govservice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 成功响应
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(200, "操作成功", data);
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(200, "操作成功", null);
    }
    
    /**
     * 失败响应
     */
    public static <T> CommonResponse<T> error(Integer code, String message) {
        return new CommonResponse<>(code, message, null);
    }
    
    /**
     * 失败响应（默认错误码）
     */
    public static <T> CommonResponse<T> error(String message) {
        return new CommonResponse<>(500, message, null);
    }
}
