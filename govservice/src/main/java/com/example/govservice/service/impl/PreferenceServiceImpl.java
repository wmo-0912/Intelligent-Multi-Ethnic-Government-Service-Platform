package com.example.govservice.service.impl;

import com.example.govservice.service.PreferenceService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PreferenceServiceImpl implements PreferenceService {
    private final Map<Integer, String> userLanguage = new ConcurrentHashMap<>();

    @Override
    public void setUserLanguage(Integer userId, String languageType) {
        if (userId == null || languageType == null) return;
        userLanguage.put(userId, languageType);
    }

    @Override
    public String getUserLanguage(Integer userId) {
        if (userId == null) return null;
        return userLanguage.get(userId);
    }
}


