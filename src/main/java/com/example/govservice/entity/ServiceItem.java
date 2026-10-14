package com.example.govservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 政务事项实体类
 * 对应数据库表：ServiceItem
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItem {
    
    /**
     * 事项ID
     */
    private Integer itemId;
    
    /**
     * 事项名称
     */
    private String itemName;
    
    /**
     * 所属部门
     */
    private String departmentName;
    
    /**
     * 办理要求
     */
    private String handleRequirement;
    
    /**
     * 是否启用
     */
    private Boolean isEnabled;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
