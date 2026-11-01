package com.example.govservice.controller;

import com.example.govservice.config.annotation.GovOnly;
import com.example.govservice.dto.request.ServiceApplyRequest;
import com.example.govservice.dto.response.CommonResponse;
import com.example.govservice.dto.response.ServiceItemResponse;
import com.example.govservice.entity.HandlingRecord;
import com.example.govservice.entity.ServiceItem;
import com.example.govservice.entity.MultilingualInfo;
import com.example.govservice.service.ServiceItemService;
import com.example.govservice.service.impl.MultilingualServiceImpl;
import com.example.govservice.util.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 政务事项控制器
 */
@Slf4j
@RestController
@RequestMapping("/service-item")
@Validated
public class ServiceItemController {
    
    @Autowired
    private ServiceItemService serviceItemService;

    @Autowired
    private MultilingualServiceImpl multilingualService;
    
    /**
     * 根据事项ID查询事项详情
     */
    @GetMapping("/{itemId}")
    public CommonResponse<ServiceItemResponse> getServiceItemById(@PathVariable Integer itemId,
                                                                 @RequestParam(required = false) String languageType) {
        log.info("查询事项详情，事项ID：{}，语言类型：{}", itemId, languageType);
        ServiceItem serviceItem = serviceItemService.getServiceItemById(itemId);
        ServiceItemResponse response = convertToResponse(serviceItem);
        if (languageType != null && serviceItem != null) {
            MultilingualInfo info = null;
            try { 
                info = multilingualService.getMultilingualInfo(itemId, languageType);
                log.info("获取到多语言信息：{}", info);
            } catch (Exception e) {
                log.error("获取多语言信息失败：itemId={}, languageType={}, error={}", itemId, languageType, e.getMessage());
            }
            if (info != null) {
                log.info("应用多语言翻译：原名称={}, 翻译名称={}", response.getItemName(), info.getItemNameTrans());
                response.setItemName(info.getItemNameTrans());
                response.setHandleRequirement(info.getRequirementTrans());
            } else {
                log.warn("未找到多语言翻译数据：itemId={}, languageType={}", itemId, languageType);
            }
        }
        return CommonResponse.success(response);
    }
    
    /**
     * 查询所有启用的事项
     */
    @GetMapping("/enabled")
    public CommonResponse<List<ServiceItemResponse>> getAllEnabledServiceItems(@RequestParam(required = false) String languageType,
                                                                              @RequestParam(required = false) String keyword,
                                                                              @RequestParam(required = false) String department) {
        log.info("查询所有启用的事项");
        List<ServiceItem> serviceItems;
        if (keyword != null || department != null) {
            serviceItems = ((com.example.govservice.service.impl.ServiceItemServiceImpl)serviceItemService).search(keyword, department, true);
        } else {
            serviceItems = serviceItemService.getAllEnabledServiceItems();
        }
        List<ServiceItemResponse> responses = serviceItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        if (languageType != null) {
            for (ServiceItemResponse r : responses) {
                MultilingualInfo info = null;
                try { info = multilingualService.getMultilingualInfo(r.getItemId(), languageType); } catch (Exception ignored) {}
                if (info != null) {
                    r.setItemName(info.getItemNameTrans());
                    r.setHandleRequirement(info.getRequirementTrans());
                }
            }
        }
        return CommonResponse.success(responses);
    }

    /**
     * 统一搜索（支持分页）
     */
    @GetMapping("/search")
    public CommonResponse<com.example.govservice.dto.response.PageResponse<ServiceItemResponse>> search(
            @RequestParam(required = false) String languageType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Boolean onlyEnabled,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        int offset = (p - 1) * s;
        long total = ((com.example.govservice.service.impl.ServiceItemServiceImpl)serviceItemService)
                .countSearch(keyword, department, onlyEnabled);
        List<ServiceItem> list = ((com.example.govservice.service.impl.ServiceItemServiceImpl)serviceItemService)
                .pageSearch(keyword, department, onlyEnabled, offset, s);
        List<ServiceItemResponse> records = list.stream().map(this::convertToResponse).collect(Collectors.toList());
        if (languageType != null) {
            for (ServiceItemResponse r : records) {
                MultilingualInfo info = null;
                try { info = multilingualService.getMultilingualInfo(r.getItemId(), languageType); } catch (Exception ignored) {}
                if (info != null) {
                    r.setItemName(info.getItemNameTrans());
                    r.setHandleRequirement(info.getRequirementTrans());
                }
            }
        }
        com.example.govservice.dto.response.PageResponse<ServiceItemResponse> resp =
                new com.example.govservice.dto.response.PageResponse<>(total, p, s, records);
        return CommonResponse.success(resp);
    }
    
    /**
     * 查询所有事项（政务人员功能）
     */
    @GetMapping("/all")
    public CommonResponse<List<ServiceItemResponse>> getAllServiceItems() {
        log.info("查询所有事项");
        List<ServiceItem> serviceItems = serviceItemService.getAllServiceItems();
        List<ServiceItemResponse> responses = serviceItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return CommonResponse.success(responses);
    }
    
    /**
     * 根据部门查询事项
     */
    @GetMapping("/department/{departmentName}")
    public CommonResponse<List<ServiceItemResponse>> getServiceItemsByDepartment(
            @PathVariable String departmentName,
            @RequestParam(required = false) String languageType) {
        log.info("根据部门查询事项，部门：{}", departmentName);
        List<ServiceItem> serviceItems = serviceItemService.getServiceItemsByDepartment(departmentName);
        List<ServiceItemResponse> responses = serviceItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        if (languageType != null) {
            for (ServiceItemResponse r : responses) {
                MultilingualInfo info = null;
                try { info = multilingualService.getMultilingualInfo(r.getItemId(), languageType); } catch (Exception ignored) {}
                if (info != null) {
                    r.setItemName(info.getItemNameTrans());
                    r.setHandleRequirement(info.getRequirementTrans());
                }
            }
        }
        return CommonResponse.success(responses);
    }
    
    /**
     * 根据部门查询启用的事项
     */
    @GetMapping("/department/{departmentName}/enabled")
    public CommonResponse<List<ServiceItemResponse>> getEnabledServiceItemsByDepartment(
            @PathVariable String departmentName,
            @RequestParam(required = false) String languageType) {
        log.info("根据部门查询启用的事项，部门：{}", departmentName);
        List<ServiceItem> serviceItems = serviceItemService.getEnabledServiceItemsByDepartment(departmentName);
        List<ServiceItemResponse> responses = serviceItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        if (languageType != null) {
            for (ServiceItemResponse r : responses) {
                MultilingualInfo info = null;
                try { info = multilingualService.getMultilingualInfo(r.getItemId(), languageType); } catch (Exception ignored) {}
                if (info != null) {
                    r.setItemName(info.getItemNameTrans());
                    r.setHandleRequirement(info.getRequirementTrans());
                }
            }
        }
        return CommonResponse.success(responses);
    }
    
    /**
     * 创建事项（政务人员功能）
     */
    @GovOnly
    @PostMapping
    public CommonResponse<ServiceItemResponse> createServiceItem(@Valid @RequestBody ServiceItem serviceItem) {
        log.info("创建事项：{}", serviceItem.getItemName());
        ServiceItem createdItem = serviceItemService.createServiceItem(serviceItem);
        ServiceItemResponse response = convertToResponse(createdItem);
        return CommonResponse.success(response);
    }
    
    /**
     * 更新事项信息（政务人员功能）
     */
    @GovOnly
    @PutMapping("/{itemId}")
    public CommonResponse<ServiceItemResponse> updateServiceItem(@PathVariable Integer itemId,
                                                               @Valid @RequestBody ServiceItem serviceItem) {
        log.info("更新事项信息，事项ID：{}", itemId);
        serviceItem.setItemId(itemId);
        ServiceItem updatedItem = serviceItemService.updateServiceItem(serviceItem);
        ServiceItemResponse response = convertToResponse(updatedItem);
        return CommonResponse.success(response);
    }
    
    /**
     * 启用/禁用事项（政务人员功能）
     */
    @GovOnly
    @PutMapping("/{itemId}/status")
    public CommonResponse<Void> updateServiceItemStatus(@PathVariable Integer itemId,
                                                       @RequestParam Boolean isEnabled) {
        log.info("更新事项状态，事项ID：{}，状态：{}", itemId, isEnabled);
        serviceItemService.updateServiceItemStatus(itemId, isEnabled);
        return CommonResponse.success();
    }
    
    /**
     * 删除事项（政务人员功能）
     */
    @GovOnly
    @DeleteMapping("/{itemId}")
    public CommonResponse<Void> deleteServiceItem(@PathVariable Integer itemId) {
        log.info("删除事项，事项ID：{}", itemId);
        serviceItemService.deleteServiceItem(itemId);
        return CommonResponse.success();
    }

    private boolean hasGovRole() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return false;
        Object role = attrs.getRequest().getAttribute("jwt.role");
        return "政务人员".equals(String.valueOf(role));
    }
    
    /**
     * 提交事项申请
     */
    @PostMapping("/apply")
    public CommonResponse<HandlingRecord> submitApplication(@Valid @RequestBody ServiceApplyRequest request) {
        log.info("提交事项申请，用户ID：{}，事项ID：{}", request.getUserId(), request.getItemId());
        HandlingRecord record = serviceItemService.submitApplication(request);
        return CommonResponse.success(record);
    }
    
    /**
     * 根据用户ID查询申请记录
     */
    @GetMapping("/applications/user/{userId}")
    public CommonResponse<List<com.example.govservice.dto.response.HandlingRecordResponse>> getApplicationsByUserId(@PathVariable Integer userId,
                                                                                                                    @RequestParam(required = false) String languageType) {
        log.info("查询用户申请记录，用户ID：{}", userId);
        List<HandlingRecord> records = serviceItemService.getApplicationsByUserId(userId);
        List<com.example.govservice.dto.response.HandlingRecordResponse> resp = records.stream().map(r -> {
            com.example.govservice.dto.response.HandlingRecordResponse x = new com.example.govservice.dto.response.HandlingRecordResponse();
            x.setRecordId(r.getRecordId());
            x.setUserId(r.getUserId());
            x.setItemId(r.getItemId());
            x.setStatus(I18nUtils.translateStatus(languageType, r.getStatus()));
            x.setSubmitTime(r.getSubmitTime());
            x.setHandleResult(r.getHandleResult());
            x.setHandleTime(r.getHandleTime());
            x.setHandleUserId(r.getHandleUserId());
            x.setApplicationMaterials(r.getApplicationMaterials());
            return x;
        }).collect(Collectors.toList());
        return CommonResponse.success(resp);
    }
    
    /**
     * 查询当前用户的申请记录（从token获取userId）
     */
    @GetMapping("/applications/me")
    public CommonResponse<List<com.example.govservice.dto.response.HandlingRecordResponse>> getMyApplications(
            @RequestParam(required = false) String languageType,
            javax.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("jwt.userId");
        Integer userId = userIdObj == null ? null : Integer.valueOf(String.valueOf(userIdObj));
        
        log.info("查询当前用户申请记录，用户ID：{}", userId);
        List<HandlingRecord> records = serviceItemService.getApplicationsByUserId(userId);
        List<com.example.govservice.dto.response.HandlingRecordResponse> resp = records.stream().map(r -> {
            com.example.govservice.dto.response.HandlingRecordResponse x = new com.example.govservice.dto.response.HandlingRecordResponse();
            x.setRecordId(r.getRecordId());
            x.setUserId(r.getUserId());
            x.setItemId(r.getItemId());
            x.setStatus(I18nUtils.translateStatus(languageType, r.getStatus()));
            x.setSubmitTime(r.getSubmitTime());
            x.setHandleResult(r.getHandleResult());
            x.setHandleTime(r.getHandleTime());
            x.setHandleUserId(r.getHandleUserId());
            x.setApplicationMaterials(r.getApplicationMaterials());
            return x;
        }).collect(Collectors.toList());
        return CommonResponse.success(resp);
    }
    
    /**
     * 查询待处理的申请记录（政务人员功能）
     */
    @GetMapping("/applications/pending")
    public CommonResponse<List<com.example.govservice.dto.response.HandlingRecordResponse>> getPendingApplications(@RequestParam(required = false) String languageType) {
        log.info("查询待处理的申请记录");
        List<HandlingRecord> records = serviceItemService.getPendingApplications();
        List<com.example.govservice.dto.response.HandlingRecordResponse> resp = records.stream().map(r -> {
            com.example.govservice.dto.response.HandlingRecordResponse x = new com.example.govservice.dto.response.HandlingRecordResponse();
            x.setRecordId(r.getRecordId());
            x.setUserId(r.getUserId());
            x.setItemId(r.getItemId());
            x.setStatus(I18nUtils.translateStatus(languageType, r.getStatus()));
            x.setSubmitTime(r.getSubmitTime());
            x.setHandleResult(r.getHandleResult());
            x.setHandleTime(r.getHandleTime());
            x.setHandleUserId(r.getHandleUserId());
            x.setApplicationMaterials(r.getApplicationMaterials());
            return x;
        }).collect(Collectors.toList());
        return CommonResponse.success(resp);
    }
    
    /**
     * 处理申请（审核/驳回）（政务人员功能）
     */
    @PutMapping("/applications/{recordId}/handle")
    public CommonResponse<Void> handleApplication(@PathVariable Integer recordId,
                                                 @RequestParam String status,
                                                 @RequestParam String handleResult,
                                                 @RequestParam Integer handleUserId) {
        log.info("处理申请，记录ID：{}，状态：{}", recordId, status);
        serviceItemService.handleApplication(recordId, status, handleResult, handleUserId);
        return CommonResponse.success();
    }
    
    /**
     * 统计办理量
     */
    @GetMapping("/{itemId}/statistics")
    public CommonResponse<Object> getStatistics(@PathVariable Integer itemId) {
        log.info("统计事项办理量，事项ID：{}", itemId);
        int totalCount = serviceItemService.countApplicationsByItemId(itemId);
        int completedCount = serviceItemService.countCompletedApplicationsByItemId(itemId);
        
        Object statistics = new Object() {
            public final int totalApplications = totalCount;
            public final int completedApplications = completedCount;
            public final double completionRate = totalCount > 0 ? (double) completedCount / totalCount : 0.0;
        };
        
        return CommonResponse.success(statistics);
    }
    
    /**
     * 转换ServiceItem为ServiceItemResponse
     */
    private ServiceItemResponse convertToResponse(ServiceItem serviceItem) {
        ServiceItemResponse response = new ServiceItemResponse();
        BeanUtils.copyProperties(serviceItem, response);
        return response;
    }
}
