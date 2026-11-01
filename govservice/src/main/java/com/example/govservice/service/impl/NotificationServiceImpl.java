package com.example.govservice.service.impl;

import com.example.govservice.dto.response.PageResponse;
import com.example.govservice.entity.Notification;
import com.example.govservice.mapper.NotificationMapper;
import com.example.govservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知服务实现
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Override
    public void sendNotification(Integer userId, String title, String content, String notifyType, Integer relatedRecordId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotifyType(notifyType);
        notification.setRelatedRecordId(relatedRecordId);
        
        notificationMapper.insert(notification);
        log.info("通知已发送：userId={}, title={}, type={}", userId, title, notifyType);
        
        // TODO: 如果需要短信通知，在此处调用短信服务
        // smsService.sendSms(userPhone, content);
    }
    
    @Override
    public PageResponse<Notification> getUserNotifications(Integer userId, int page, int size) {
        int p = page < 1 ? 1 : page;
        int s = size < 1 ? 10 : size;
        int offset = (p - 1) * s;
        
        long total = notificationMapper.countByUserId(userId);
        List<Notification> records = notificationMapper.selectByUserId(userId, offset, s);
        
        return new PageResponse<>(total, p, s, records);
    }
    
    @Override
    public List<Notification> getNotificationsByUserId(Integer userId) {
        // 查询所有通知（最多100条，按时间倒序）
        return notificationMapper.selectByUserId(userId, 0, 100);
    }
    
    @Override
    public long getUnreadCount(Integer userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }
    
    @Override
    public void markAsRead(Integer notificationId) {
        notificationMapper.markAsRead(notificationId);
        log.info("消息已标记为已读：notificationId={}", notificationId);
    }
    
    @Override
    public void markAllAsRead(Integer userId) {
        notificationMapper.markAllAsRead(userId);
        log.info("所有消息已标记为已读：userId={}", userId);
    }
}

