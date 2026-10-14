package com.example.govservice.service.impl;

import com.example.govservice.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token黑名单服务实现（内存版本）
 * TODO: 生产环境建议使用Redis实现，支持分布式和持久化
 */
@Slf4j
@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    
    // 黑名单存储 <token, 过期时间戳>
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();
    
    // Token有效期8小时（与JwtUtils保持一致）
    private static final long TOKEN_EXPIRE_MS = 8 * 60 * 60 * 1000;
    
    @Override
    public void addToBlacklist(String token) {
        long expireTime = System.currentTimeMillis() + TOKEN_EXPIRE_MS;
        blacklist.put(token, expireTime);
        log.info("Token已加入黑名单，当前黑名单大小：{}", blacklist.size());
    }
    
    @Override
    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }
    
    @Override
    public void cleanupExpired() {
        long now = System.currentTimeMillis();
        int beforeSize = blacklist.size();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
        int afterSize = blacklist.size();
        if (beforeSize != afterSize) {
            log.info("清理过期黑名单Token：{} -> {}", beforeSize, afterSize);
        }
    }
}

