package com.example.govservice.service;

public interface PreferenceService {
    void setUserLanguage(Integer userId, String languageType);
    String getUserLanguage(Integer userId);
}


