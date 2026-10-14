package com.example.govservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 加密工具类测试
 */
class EncryptUtilsTest {

    @Test
    void testMd5() {
        String plaintext = "123456";
        String encrypted = EncryptUtils.md5(plaintext);
        
        assertNotNull(encrypted);
        assertEquals(32, encrypted.length());
        // MD5 是确定的
        assertEquals("e10adc3949ba59abbe56e057f20f883e", encrypted);
    }

    @Test
    void testMd5Consistency() {
        String plaintext = "test";
        String encrypted1 = EncryptUtils.md5(plaintext);
        String encrypted2 = EncryptUtils.md5(plaintext);
        
        assertEquals(encrypted1, encrypted2);
    }

    @Test
    void testMd5DifferentInputs() {
        String encrypted1 = EncryptUtils.md5("password1");
        String encrypted2 = EncryptUtils.md5("password2");
        
        assertNotEquals(encrypted1, encrypted2);
    }
}

