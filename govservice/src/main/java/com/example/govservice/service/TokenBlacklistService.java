package com.example.govservice.service;

/**
 * Token黑名单服务接口（用于登出失效）
 */
public interface TokenBlacklistService {
    
    /**
     * 将token加入黑名单
     */
    void addToBlacklist(String token);
    
    /**
     * 检查token是否在黑名单中
     */
    boolean isBlacklisted(String token);
    
    /**
     * 清理过期的黑名单token
     */
    void cleanupExpired();
}

