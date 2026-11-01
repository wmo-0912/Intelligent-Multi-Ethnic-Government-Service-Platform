package com.example.govservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer userId;
    private String name;
    private String role;
    private List<String> permissions;
    private String languageType; // 当前语言偏好（如 zh-CN, ug, bo, mn ...）
    private Map<String, Object> i18nConfig; // 界面多语言配置（可扩展）
}


