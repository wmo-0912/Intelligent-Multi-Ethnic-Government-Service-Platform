package com.example.govservice.controller;

import com.example.govservice.dto.response.CommonResponse;
import com.example.govservice.entity.Notification;
import com.example.govservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 通知消息控制器
 */
@Slf4j
@RestController
@RequestMapping("/notification")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 查询当前用户的通知列表
     */
    @GetMapping("/my")
    public CommonResponse<java.util.List<Notification>> getMyNotifications(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("jwt.userId");
        Integer userId = userIdObj == null ? null : Integer.valueOf(String.valueOf(userIdObj));
        
        java.util.List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return CommonResponse.success(notifications);
    }
    
    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread-count")
    public CommonResponse<Long> getUnreadCount(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("jwt.userId");
        Integer userId = userIdObj == null ? null : Integer.valueOf(String.valueOf(userIdObj));
        
        long count = notificationService.getUnreadCount(userId);
        return CommonResponse.success(count);
    }
    
    /**
     * 标记消息为已读
     */
    @PutMapping("/{notificationId}/read")
    public CommonResponse<Void> markAsRead(@PathVariable Integer notificationId) {
        notificationService.markAsRead(notificationId);
        return CommonResponse.success();
    }
    
    /**
     * 标记所有消息为已读
     */
    @PutMapping("/read-all")
    public CommonResponse<Void> markAllAsRead(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("jwt.userId");
        Integer userId = userIdObj == null ? null : Integer.valueOf(String.valueOf(userIdObj));
        
        notificationService.markAllAsRead(userId);
        return CommonResponse.success();
    }
}

