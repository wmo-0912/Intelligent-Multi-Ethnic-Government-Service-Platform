package com.example.govservice.service.impl;

import com.example.govservice.dto.request.ServiceApplyRequest;
import com.example.govservice.entity.HandlingRecord;
import com.example.govservice.entity.ServiceItem;
import com.example.govservice.exception.BusinessException;
import com.example.govservice.exception.ErrorCode;
import com.example.govservice.mapper.HandlingRecordMapper;
import com.example.govservice.mapper.ServiceItemMapper;
import com.example.govservice.service.NotificationService;
import com.example.govservice.service.ServiceItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 政务事项服务实现类
 */
@Slf4j
@Service
public class ServiceItemServiceImpl implements ServiceItemService {
    
    @Autowired
    private ServiceItemMapper serviceItemMapper;
    
    @Autowired
    private HandlingRecordMapper handlingRecordMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private com.example.govservice.service.PreferenceService preferenceService;
    
    @Autowired
    private com.example.govservice.mapper.MultilingualMapper multilingualMapper;
    
    @Override
    public ServiceItem getServiceItemById(Integer itemId) {
        ServiceItem serviceItem = serviceItemMapper.selectById(itemId);
        if (serviceItem == null) {
            throw new BusinessException(ErrorCode.SERVICE_ITEM_NOT_FOUND);
        }
        return serviceItem;
    }
    
    @Override
    public List<ServiceItem> getAllEnabledServiceItems() {
        return serviceItemMapper.selectAllEnabled();
    }
    
    @Override
    public List<ServiceItem> getAllServiceItems() {
        return serviceItemMapper.selectAll();
    }
    
    @Override
    public List<ServiceItem> getServiceItemsByDepartment(String departmentName) {
        return serviceItemMapper.selectByDepartment(departmentName);
    }
    
    @Override
    public List<ServiceItem> getEnabledServiceItemsByDepartment(String departmentName) {
        return serviceItemMapper.selectEnabledByDepartment(departmentName);
    }

    // 新增：关键词搜索
    public List<ServiceItem> search(String keyword, String departmentName, Boolean onlyEnabled) {
        return serviceItemMapper.search(keyword, departmentName, onlyEnabled);
    }

    public long countSearch(String keyword, String departmentName, Boolean onlyEnabled) {
        return serviceItemMapper.countSearch(keyword, departmentName, onlyEnabled);
    }

    public List<ServiceItem> pageSearch(String keyword, String departmentName, Boolean onlyEnabled, int offset, int limit) {
        return serviceItemMapper.pageSearch(keyword, departmentName, onlyEnabled, offset, limit);
    }
    
    @Override
    public ServiceItem createServiceItem(ServiceItem serviceItem) {
        serviceItem.setCreateTime(LocalDateTime.now());
        serviceItem.setUpdateTime(LocalDateTime.now());
        serviceItem.setIsEnabled(true);
        
        int result = serviceItemMapper.insert(serviceItem);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "事项创建失败");
        }
        
        log.info("事项创建成功，事项ID：{}", serviceItem.getItemId());
        return serviceItem;
    }
    
    @Override
    public ServiceItem updateServiceItem(ServiceItem serviceItem) {
        // 检查事项是否存在
        ServiceItem existingItem = serviceItemMapper.selectById(serviceItem.getItemId());
        if (existingItem == null) {
            throw new BusinessException(ErrorCode.SERVICE_ITEM_NOT_FOUND);
        }
        
        serviceItem.setUpdateTime(LocalDateTime.now());
        
        int result = serviceItemMapper.update(serviceItem);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "事项更新失败");
        }
        
        log.info("事项更新成功，事项ID：{}", serviceItem.getItemId());
        return serviceItem;
    }
    
    @Override
    public boolean updateServiceItemStatus(Integer itemId, Boolean isEnabled) {
        // 检查事项是否存在
        ServiceItem serviceItem = serviceItemMapper.selectById(itemId);
        if (serviceItem == null) {
            throw new BusinessException(ErrorCode.SERVICE_ITEM_NOT_FOUND);
        }
        
        int result = serviceItemMapper.updateStatus(itemId, isEnabled);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "事项状态更新失败");
        }
        
        log.info("事项状态更新成功，事项ID：{}，状态：{}", itemId, isEnabled);
        return true;
    }
    
    @Override
    public boolean deleteServiceItem(Integer itemId) {
        // 检查事项是否存在
        ServiceItem serviceItem = serviceItemMapper.selectById(itemId);
        if (serviceItem == null) {
            throw new BusinessException(ErrorCode.SERVICE_ITEM_NOT_FOUND);
        }
        
        int result = serviceItemMapper.deleteById(itemId);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "事项删除失败");
        }
        
        log.info("事项删除成功，事项ID：{}", itemId);
        return true;
    }
    
    @Override
    public HandlingRecord submitApplication(ServiceApplyRequest request) {
        // 检查事项是否存在且启用
        ServiceItem serviceItem = serviceItemMapper.selectById(request.getItemId());
        if (serviceItem == null) {
            throw new BusinessException(ErrorCode.SERVICE_ITEM_NOT_FOUND);
        }
        if (!serviceItem.getIsEnabled()) {
            throw new BusinessException(ErrorCode.SERVICE_ITEM_DISABLED);
        }
        
        // 创建申请记录
        HandlingRecord record = new HandlingRecord();
        record.setUserId(request.getUserId());
        record.setItemId(request.getItemId());
        record.setStatus("待审核");
        record.setSubmitTime(LocalDateTime.now());
        record.setApplicationMaterials(request.getApplicationMaterials());
        
        int result = handlingRecordMapper.insert(record);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "申请提交失败");
        }
        
        log.info("申请提交成功，记录ID：{}", record.getRecordId());
        
        // 发送申请提交成功通知（多语言）
        try {
            // 获取用户语言偏好
            String userLang = preferenceService.getUserLanguage(request.getUserId());
            if (userLang == null) userLang = "zh-CN";
            
            // 获取事项名称（多语言）
            String itemName = serviceItem.getItemName();
            if (!"zh-CN".equals(userLang)) {
                try {
                    com.example.govservice.entity.MultilingualInfo mlInfo = 
                        multilingualMapper.selectByItemIdAndLanguage(serviceItem.getItemId(), userLang);
                    if (mlInfo != null && mlInfo.getItemNameTrans() != null && !mlInfo.getItemNameTrans().isEmpty()) {
                        itemName = mlInfo.getItemNameTrans();
                    }
                } catch (Exception ignored) {}
            }
            
            // 生成多语言通知内容
            String title = com.example.govservice.util.I18nUtils.getMessage(userLang, "application.submit.success", "申请提交成功");
            String contentTemplate = com.example.govservice.util.I18nUtils.getMessage(userLang, "notification.application.submitted", 
                "您的【{itemName}】申请已提交，请等待审核。");
            String content = contentTemplate.replace("{itemName}", itemName);
            
            notificationService.sendNotification(request.getUserId(), title, content, "APPLICATION_STATUS", record.getRecordId());
        } catch (Exception e) {
            log.error("发送申请提交通知失败", e);
        }
        
        return record;
    }
    
    @Override
    public List<HandlingRecord> getApplicationsByUserId(Integer userId) {
        return handlingRecordMapper.selectByUserId(userId);
    }
    
    @Override
    public List<HandlingRecord> getPendingApplications() {
        return handlingRecordMapper.selectPendingRecords();
    }
    
    @Override
    public boolean handleApplication(Integer recordId, String status, String handleResult, Integer handleUserId) {
        // 检查申请记录是否存在
        HandlingRecord record = handlingRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(ErrorCode.APPLICATION_NOT_FOUND);
        }
        
        // 状态流转校验
        String currentStatus = record.getStatus();
        if (!isValidStatusTransition(currentStatus, status)) {
            throw new BusinessException(ErrorCode.APPLICATION_STATUS_ERROR.getCode(), 
                "非法的状态流转：" + currentStatus + " -> " + status);
        }
        
        int result = handlingRecordMapper.updateStatus(recordId, status, handleResult, handleUserId);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "申请处理失败");
        }
        
        log.info("申请处理成功，记录ID：{}，状态：{}", recordId, status);
        
        // 发送状态变更通知（多语言）
        try {
            // 获取用户语言偏好
            String userLang = preferenceService.getUserLanguage(record.getUserId());
            if (userLang == null) userLang = "zh-CN";
            
            // 生成多语言通知内容
            String title = com.example.govservice.util.I18nUtils.getMessage(userLang, "notification.status.changed", "申请状态变更");
            String translatedStatus = com.example.govservice.util.I18nUtils.translateStatus(userLang, status);
            String contentTemplate = com.example.govservice.util.I18nUtils.getMessage(userLang, "notification.status.changed.content", 
                "您的申请状态已变更为【{status}】");
            String content = contentTemplate.replace("{status}", translatedStatus);
            
            if (handleResult != null && !handleResult.isEmpty()) {
                String resultPrefix = com.example.govservice.util.I18nUtils.getMessage(userLang, "notification.handle.result", "，处理结果：");
                content += resultPrefix + handleResult;
            }
            
            notificationService.sendNotification(record.getUserId(), title, content, "APPLICATION_STATUS", recordId);
        } catch (Exception e) {
            log.error("发送状态变更通知失败", e);
        }
        
        return true;
    }
    
    @Override
    public int countApplicationsByItemId(Integer itemId) {
        return handlingRecordMapper.countByItemId(itemId);
    }
    
    @Override
    public int countCompletedApplicationsByItemId(Integer itemId) {
        return handlingRecordMapper.countCompletedByItemId(itemId);
    }
    
    /**
     * 状态流转校验
     * 合法流转：待审核 -> 处理中 -> 已完成/已通过/驳回
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // 待审核 -> 处理中、已完成、已通过、驳回
        if ("待审核".equals(currentStatus)) {
            return "处理中".equals(newStatus) || "已完成".equals(newStatus) 
                || "已通过".equals(newStatus) || "驳回".equals(newStatus);
        }
        // 处理中 -> 已完成、已通过、驳回
        if ("处理中".equals(currentStatus)) {
            return "已完成".equals(newStatus) || "已通过".equals(newStatus) || "驳回".equals(newStatus);
        }
        // 已完成、已通过和驳回状态不能再流转
        return false;
    }
}
