package com.example.govservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * FAQ实体类
 * 对应数据库表：FAQ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Faq {
    
    /**
     * 问题ID
     */
    private Integer questionId;
    
    /**
     * 关联事项ID
     */
    private Integer itemId;
    
    /**
     * 问题
     */
    private String question;
    
    /**
     * 答案
     */
    private String answer;
    
    /**
     * 语种
     */
    private String languageType;
    
    /**
     * 访问次数
     */
    private Integer visitCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
