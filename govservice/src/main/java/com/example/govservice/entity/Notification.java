package com.example.govservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 站内通知实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Integer notificationId;
    private Integer userId;           // 接收用户ID
    private String title;             // 通知标题
    private String content;           // 通知内容
    private String notifyType;        // 通知类型（APPLICATION_STATUS/SYSTEM/ANNOUNCEMENT）
    private Integer relatedRecordId;  // 关联办理记录ID
    private Boolean isRead;           // 是否已读
    private Date createTime;
    private Date readTime;            // 阅读时间
}

