package com.example.govservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandlingRecordResponse {
    private Integer recordId;
    private Integer userId;
    private Integer itemId;
    private String status; // 已按语言翻译
    private LocalDateTime submitTime;
    private String handleResult;
    private LocalDateTime handleTime;
    private Integer handleUserId;
    private String applicationMaterials;
}


