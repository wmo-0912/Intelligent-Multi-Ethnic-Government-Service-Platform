package com.example.govservice.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 事项申请请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceApplyRequest {
    
    /**
     * 申请人ID
     */
    @NotNull(message = "申请人ID不能为空")
    private Integer userId;
    
    /**
     * 事项ID
     */
    @NotNull(message = "事项ID不能为空")
    private Integer itemId;
    
    /**
     * 申请材料信息
     */
    @NotBlank(message = "申请材料不能为空")
    private String applicationMaterials;
}
