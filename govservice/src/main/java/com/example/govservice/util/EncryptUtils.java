package com.example.govservice.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 加密工具类
 */
public class EncryptUtils {
    
    /**
     * MD5加密
     */
    public static String md5(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
    
    /**
     * SHA-256加密
     */
    public static String sha256(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256加密失败", e);
        }
    }
    
    /**
     * 生成UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 生成带连字符的UUID
     */
    public static String generateUUIDWithHyphens() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 简单的密码加密（MD5 + 盐值）
     */
    public static String encryptPassword(String password, String salt) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        if (salt == null) {
            salt = "";
        }
        return md5(password + salt);
    }
    
    /**
     * 验证密码
     */
    public static boolean verifyPassword(String password, String salt, String encryptedPassword) {
        if (password == null || encryptedPassword == null) {
            return false;
        }
        String encrypted = encryptPassword(password, salt);
        return encryptedPassword.equals(encrypted);
    }
}
