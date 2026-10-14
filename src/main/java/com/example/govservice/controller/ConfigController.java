package com.example.govservice.controller;

import com.example.govservice.dto.response.CommonResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private final Set<String> supportedLanguages = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<String, String> notifyTemplates = new ConcurrentHashMap<>();

    public ConfigController() {
        supportedLanguages.addAll(Arrays.asList("zh-CN", "ug", "bo", "mn", "kk", "ko"));
        notifyTemplates.put("default.sms", "【政务平台】您的办理状态已更新：${status}");
        notifyTemplates.put("default.push", "您有新的办理通知，请及时查看");
    }

    @GetMapping("/languages")
    public CommonResponse<Set<String>> listLanguages() {
        return CommonResponse.success(supportedLanguages);
    }

    @PostMapping("/languages")
    public CommonResponse<Void> addLanguage(@RequestParam String code) {
        supportedLanguages.add(code);
        return CommonResponse.success();
    }

    @DeleteMapping("/languages/{code}")
    public CommonResponse<Void> removeLanguage(@PathVariable String code) {
        supportedLanguages.remove(code);
        return CommonResponse.success();
    }

    @GetMapping("/templates")
    public CommonResponse<Map<String, String>> listTemplates() {
        return CommonResponse.success(notifyTemplates);
    }

    @PutMapping("/templates/{key}")
    public CommonResponse<Void> updateTemplate(@PathVariable String key, @RequestBody String content) {
        notifyTemplates.put(key, content);
        return CommonResponse.success();
    }
}


