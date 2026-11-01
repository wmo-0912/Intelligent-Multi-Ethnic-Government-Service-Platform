package com.example.govservice.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 语言配置测试
 */
class LanguageConfigTest {

    @Test
    void testLanguageTypeFromCode() {
        LanguageConfig.LanguageType zhCN = LanguageConfig.LanguageType.fromCode("zh-CN");
        assertNotNull(zhCN);
        assertEquals("zh-CN", zhCN.getCode());
        assertEquals("中文", zhCN.getName());
    }

    @Test
    void testLanguageTypeIsSupported() {
        assertTrue(LanguageConfig.LanguageType.isSupported("zh-CN"));
        assertTrue(LanguageConfig.LanguageType.isSupported("mn"));
        assertTrue(LanguageConfig.LanguageType.isSupported("bo"));
        assertTrue(LanguageConfig.LanguageType.isSupported("ug"));
        assertFalse(LanguageConfig.LanguageType.isSupported("invalid"));
    }

    @Test
    void testLanguageTypeGetName() {
        assertEquals("中文", LanguageConfig.LanguageType.ZH_CN.getName());
        assertEquals("蒙古语", LanguageConfig.LanguageType.MN.getName());
        assertEquals("藏语", LanguageConfig.LanguageType.BO.getName());
    }

    @Test
    void testLanguageTypeGetCode() {
        assertEquals("zh-CN", LanguageConfig.LanguageType.ZH_CN.getCode());
        assertEquals("mn", LanguageConfig.LanguageType.MN.getCode());
        assertEquals("bo", LanguageConfig.LanguageType.BO.getCode());
    }

    @Test
    void testFromCodeWithUnknown() {
        LanguageConfig.LanguageType result = LanguageConfig.LanguageType.fromCode("unknown");
        assertNull(result);
    }
}

