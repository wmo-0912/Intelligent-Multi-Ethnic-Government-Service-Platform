package com.example.govservice.util;

import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtils {
    
    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
    
    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 判断字符串是否为空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
    
    /**
     * 判断字符串是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * 验证手机号格式
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^1[3-9]\\d{9}$");
        return pattern.matcher(phoneNumber).matches();
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        return pattern.matcher(email).matches();
    }
    
    /**
     * 验证账号格式（字母数字下划线，3-20位）
     */
    public static boolean isValidAccount(String account) {
        if (isEmpty(account)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
        return pattern.matcher(account).matches();
    }
    
    /**
     * 验证密码格式（至少6位，包含字母和数字）
     */
    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        if (password.length() < 6) {
            return false;
        }
        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).+$");
        return pattern.matcher(password).matches();
    }
    
    /**
     * 隐藏手机号中间4位
     */
    public static String maskPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber) || phoneNumber.length() != 11) {
            return phoneNumber;
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
    }
    
    /**
     * 截取字符串
     */
    public static String truncate(String str, int maxLength) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
    
    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * 首字母小写
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
