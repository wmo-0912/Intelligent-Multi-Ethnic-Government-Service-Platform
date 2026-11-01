package com.example.govservice.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 政务事项实体类测试
 */
class ServiceItemTest {

    @Test
    void testServiceItemCreation() {
        ServiceItem item = new ServiceItem();
        item.setItemId(1);
        item.setItemName("身份证办理");
        item.setDepartmentName("公安局");
        item.setHandleRequirement("需要身份证原件");
        item.setIsEnabled(true);
        
        assertNotNull(item);
        assertEquals(1, item.getItemId());
        assertEquals("身份证办理", item.getItemName());
        assertEquals("公安局", item.getDepartmentName());
        assertTrue(item.getIsEnabled());
    }

    @Test
    void testServiceItemDisabled() {
        ServiceItem item = new ServiceItem();
        item.setItemId(1);
        item.setIsEnabled(false);
        
        assertFalse(item.getIsEnabled());
    }
}

