package com.example.govservice.util;

import java.io.InputStream;
import java.util.Properties;

public class I18nUtils {

    public static String getMessage(String languageType, String key, String defaultValue) {
        if (languageType == null || key == null) return defaultValue;
        String path = "languages/" + languageType + ".properties";
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (in == null) return defaultValue;
            Properties p = new Properties();
            // 使用 UTF-8 编码读取，避免少数民族语言乱码
            p.load(new java.io.InputStreamReader(in, "UTF-8"));
            return p.getProperty(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String translateStatus(String languageType, String statusCn) {
        if (statusCn == null) return null;
        if (languageType == null || "zh-CN".equals(languageType) || "zh".equals(languageType)) {
            // 中文直接返回
            return statusCn;
        }
        String key;
        switch (statusCn) {
            case "待处理":
            case "待审核": key = "application.pending"; break;
            case "处理中": key = "application.processing"; break;
            case "已完成":
            case "已通过": key = "application.completed"; break;
            case "驳回":
            case "已驳回": key = "application.rejected"; break;
            default: return statusCn; // 未知状态直接返回
        }
        String translated = getMessage(languageType, key, statusCn);
        return translated != null ? translated : statusCn;
    }
}


