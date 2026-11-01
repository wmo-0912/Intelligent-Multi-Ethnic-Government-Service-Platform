package com.example.govservice.service;

import com.example.govservice.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {
    
    /**
     * 上传文件
     */
    FileInfo uploadFile(MultipartFile file, Integer uploadUserId, Integer relatedRecordId, String fileCategory);
    
    /**
     * 根据文件ID查询文件信息
     */
    FileInfo getFileById(Integer fileId);
    
    /**
     * 根据办理记录ID查询关联文件
     */
    List<FileInfo> getFilesByRecordId(Integer recordId);
    
    /**
     * 根据用户ID查询用户上传的文件
     */
    List<FileInfo> getFilesByUserId(Integer userId);
    
    /**
     * 删除文件（逻辑删除）
     */
    void deleteFile(Integer fileId);
    
    /**
     * 获取文件实际存储路径
     */
    String getActualFilePath(String storagePath);
    
    /**
     * 更新文件的关联记录ID
     */
    void updateFileRelatedRecordId(Integer fileId, Integer recordId);
}

