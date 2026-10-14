package com.example.govservice.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 语言切换请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageSwitchRequest {
    
    /**
     * 目标语种
     */
    @NotBlank(message = "目标语种不能为空")
    private String languageType;
    
    /**
     * 事项ID（可选，用于获取特定事项的多语言信息）
     */
    private Integer itemId;
}
