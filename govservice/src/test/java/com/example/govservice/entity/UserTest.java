package com.example.govservice.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户实体类测试
 */
class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        user.setUserId(1);
        user.setName("张三");
        user.setAccount("zhangsan");
        user.setPhoneNumber("13800000000");
        user.setRole("居民");
        user.setEthnicity("汉族");
        
        assertNotNull(user);
        assertEquals(1, user.getUserId());
        assertEquals("张三", user.getName());
        assertEquals("zhangsan", user.getAccount());
        assertEquals("13800000000", user.getPhoneNumber());
        assertEquals("居民", user.getRole());
        assertEquals("汉族", user.getEthnicity());
    }

    @Test
    void testUserEquality() {
        User user1 = new User();
        user1.setUserId(1);
        user1.setName("张三");
        
        User user2 = new User();
        user2.setUserId(2);
        user2.setName("李四");
        
        assertNotEquals(user1, user2);
    }

    @Test
    void testUserWithDifferentRoles() {
        User resident = new User();
        resident.setRole("居民");
        
        User staff = new User();
        staff.setRole("政务人员");
        
        assertNotEquals(resident.getRole(), staff.getRole());
    }
}

