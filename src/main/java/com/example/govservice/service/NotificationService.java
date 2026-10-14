package com.example.govservice.service;

import com.example.govservice.dto.response.PageResponse;
import com.example.govservice.entity.Notification;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 发送通知
     */
    void sendNotification(Integer userId, String title, String content, String notifyType, Integer relatedRecordId);
    
    /**
     * 查询用户通知（分页）
     */
    PageResponse<Notification> getUserNotifications(Integer userId, int page, int size);
    
    /**
     * 查询用户所有通知（不分页）
     */
    java.util.List<Notification> getNotificationsByUserId(Integer userId);
    
    /**
     * 获取未读消息数量
     */
    long getUnreadCount(Integer userId);
    
    /**
     * 标记消息为已读
     */
    void markAsRead(Integer notificationId);
    
    /**
     * 标记所有消息为已读
     */
    void markAllAsRead(Integer userId);
}

