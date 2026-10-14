package com.example.govservice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 事项详情响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItemResponse {
    
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
}
