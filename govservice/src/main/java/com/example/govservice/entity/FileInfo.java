package com.example.govservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 文件信息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {
    private Integer fileId;
    private String fileName;          // 原始文件名
    private String storagePath;       // 存储路径
    private String fileType;          // 文件类型（MIME）
    private Long fileSize;            // 文件大小（字节）
    private Integer uploadUserId;     // 上传用户ID
    private Integer relatedRecordId;  // 关联办理记录ID
    private String fileCategory;      // 文件分类（ID_CARD/PROOF/OTHER）
    private Date uploadTime;
    private Boolean deleted;          // 逻辑删除标记
}

