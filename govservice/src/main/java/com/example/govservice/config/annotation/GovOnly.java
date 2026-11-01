package com.example.govservice.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 政务人员专用接口注解
 * 用于标记仅政务人员可访问的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GovOnly {
    
    /**
     * 错误提示信息
     */
    String message() default "仅限政务人员访问";
}

