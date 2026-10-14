package com.example.govservice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 咨询回复响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaqResponse {
    
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
}
