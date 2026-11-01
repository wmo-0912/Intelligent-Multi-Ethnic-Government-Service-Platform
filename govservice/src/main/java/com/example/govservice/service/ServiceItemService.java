package com.example.govservice.service;

import com.example.govservice.dto.request.ServiceApplyRequest;
import com.example.govservice.entity.HandlingRecord;
import com.example.govservice.entity.ServiceItem;

import java.util.List;

/**
 * 政务事项服务接口
 */
public interface ServiceItemService {
    
    /**
     * 根据事项ID查询事项详情
     */
    ServiceItem getServiceItemById(Integer itemId);
    
    /**
     * 查询所有启用的事项
     */
    List<ServiceItem> getAllEnabledServiceItems();
    
    /**
     * 查询所有事项
     */
    List<ServiceItem> getAllServiceItems();
    
    /**
     * 根据部门查询事项
     */
    List<ServiceItem> getServiceItemsByDepartment(String departmentName);
    
    /**
     * 根据部门查询启用的事项
     */
    List<ServiceItem> getEnabledServiceItemsByDepartment(String departmentName);
    
    /**
     * 创建事项
     */
    ServiceItem createServiceItem(ServiceItem serviceItem);
    
    /**
     * 更新事项信息
     */
    ServiceItem updateServiceItem(ServiceItem serviceItem);
    
    /**
     * 启用/禁用事项
     */
    boolean updateServiceItemStatus(Integer itemId, Boolean isEnabled);
    
    /**
     * 删除事项
     */
    boolean deleteServiceItem(Integer itemId);
    
    /**
     * 提交事项申请
     */
    HandlingRecord submitApplication(ServiceApplyRequest request);
    
    /**
     * 根据用户ID查询申请记录
     */
    List<HandlingRecord> getApplicationsByUserId(Integer userId);
    
    /**
     * 查询待处理的申请记录
     */
    List<HandlingRecord> getPendingApplications();
    
    /**
     * 处理申请（审核/驳回）
     */
    boolean handleApplication(Integer recordId, String status, String handleResult, Integer handleUserId);
    
    /**
     * 统计办理量
     */
    int countApplicationsByItemId(Integer itemId);
    
    /**
     * 统计通过量
     */
    int countCompletedApplicationsByItemId(Integer itemId);
}
