package com.example.govservice.service;

/**
 * AI智能回答服务接口
 * 用于在FAQ找不到匹配答案时，调用AI服务生成智能回答
 */
public interface AiService {
    
    /**
     * 调用AI服务获取智能回答
     * 
     * @param question 用户问题
     * @param languageType 语言类型（如：zh-CN, ug, bo, mn等）
     * @param context 上下文信息（可选，如事项ID、相关FAQ等）
     * @return AI生成的回答
     * @throws Exception 如果AI服务调用失败
     */
    String getAiAnswer(String question, String languageType, String context) throws Exception;
    
    /**
     * 检查AI服务是否可用
     * 
     * @return true if AI服务可用，false otherwise
     */
    boolean isAvailable();
}
