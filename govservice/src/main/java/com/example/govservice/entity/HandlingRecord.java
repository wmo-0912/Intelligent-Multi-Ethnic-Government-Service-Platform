package com.example.govservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 办理记录实体类
 * 对应数据库表：HandlingRecord
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandlingRecord {
    
    /**
     * 记录ID
     */
    private Integer recordId;
    
    /**
     * 申请人ID
     */
    private Integer userId;
    
    /**
     * 事项ID
     */
    private Integer itemId;
    
    /**
     * 状态：待处理/处理中/已完成/驳回
     */
    private String status;
    
    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
    
    /**
     * 处理结果/驳回原因
     */
    private String handleResult;
    
    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
    
    /**
     * 处理人员ID
     */
    private Integer handleUserId;
    
    /**
     * 申请材料信息
     */
    private String applicationMaterials;
}
