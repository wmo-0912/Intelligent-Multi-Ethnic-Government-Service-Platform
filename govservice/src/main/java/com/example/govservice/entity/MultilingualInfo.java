package com.example.govservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 多语言信息实体类
 * 对应数据库表：MultilingualInfo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultilingualInfo {
    
    /**
     * 语言ID
     */
    private Integer languageId;
    
    /**
     * 事项ID
     */
    private Integer itemId;
    
    /**
     * 语种：如中文、维吾尔语、藏语等
     */
    private String languageType;
    
    /**
     * 事项名称翻译
     */
    private String itemNameTrans;
    
    /**
     * 办理要求翻译
     */
    private String requirementTrans;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
