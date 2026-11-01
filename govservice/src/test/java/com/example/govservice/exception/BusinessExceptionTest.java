package com.example.govservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 业务异常测试
 */
class BusinessExceptionTest {

    @Test
    void testBusinessExceptionWithErrorCode() {
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;
        BusinessException exception = new BusinessException(errorCode);
        
        assertNotNull(exception);
        assertEquals(errorCode.getCode(), exception.getCode());
        assertEquals(errorCode.getMessage(), exception.getMessage());
    }

    @Test
    void testBusinessExceptionWithMessage() {
        BusinessException exception = new BusinessException("自定义错误消息");
        
        assertNotNull(exception);
        assertEquals("自定义错误消息", exception.getMessage());
    }

    @Test
    void testBusinessExceptionWithCodeAndMessage() {
        BusinessException exception = new BusinessException(1001, "参数错误");
        
        assertNotNull(exception);
        assertEquals(1001, exception.getCode());
        assertEquals("参数错误", exception.getMessage());
    }
}

