package com.example.govservice.service.impl;

import com.example.govservice.entity.FileInfo;
import com.example.govservice.exception.BusinessException;
import com.example.govservice.exception.ErrorCode;
import com.example.govservice.mapper.FileInfoMapper;
import com.example.govservice.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件存储服务实现（本地存储）
 */
@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {
    
    @Autowired
    private FileInfoMapper fileInfoMapper;
    
    // 可配置的上传目录，默认为项目根目录下的 uploads
    @Value("${file.upload.dir:uploads}")
    private String uploadDir;
    
    // 允许的文件类型
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/jpg", "image/gif",
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    
    // 最大文件大小：10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    @Override
    public FileInfo uploadFile(MultipartFile file, Integer uploadUserId, Integer relatedRecordId, String fileCategory) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "文件不能为空");
        }
        
        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "文件大小不能超过10MB");
        }
        
        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "不支持的文件类型");
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // 创建上传目录（使用绝对路径）
        try {
            // 如果 uploadDir 不是绝对路径，则转换为绝对路径
            Path uploadPath;
            if (Paths.get(uploadDir).isAbsolute()) {
                uploadPath = Paths.get(uploadDir);
            } else {
                // 使用项目根目录（工作目录）+ uploadDir
                String projectRoot = System.getProperty("user.dir");
                uploadPath = Paths.get(projectRoot, uploadDir);
            }
            
            log.info("上传目录绝对路径：{}", uploadPath.toAbsolutePath());
            
            // 确保目录存在
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("创建上传目录：{}", uploadPath.toAbsolutePath());
            }
            
            // 保存文件
            Path filePath = uploadPath.resolve(uniqueFilename);
            file.transferTo(filePath.toFile());
            
            // 保存文件信息到数据库
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(originalFilename);
            fileInfo.setStoragePath(uniqueFilename);
            fileInfo.setFileType(contentType);
            fileInfo.setFileSize(file.getSize());
            fileInfo.setUploadUserId(uploadUserId);
            fileInfo.setRelatedRecordId(relatedRecordId);
            fileInfo.setFileCategory(fileCategory);
            
            fileInfoMapper.insert(fileInfo);
            log.info("文件上传成功：{}, fileId={}, 保存路径={}", originalFilename, fileInfo.getFileId(), filePath.toAbsolutePath());
            
            // 重新查询以获取完整信息（包括 uploadTime）
            return fileInfoMapper.selectById(fileInfo.getFileId());
            
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public FileInfo getFileById(Integer fileId) {
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "文件不存在");
        }
        return fileInfo;
    }
    
    @Override
    public List<FileInfo> getFilesByRecordId(Integer recordId) {
        return fileInfoMapper.selectByRecordId(recordId);
    }
    
    @Override
    public List<FileInfo> getFilesByUserId(Integer userId) {
        return fileInfoMapper.selectByUserId(userId);
    }
    
    @Override
    public void deleteFile(Integer fileId) {
        FileInfo fileInfo = getFileById(fileId);
        fileInfoMapper.logicDelete(fileId);
        log.info("文件已删除：fileId={}, fileName={}", fileId, fileInfo.getFileName());
    }
    
    @Override
    public String getActualFilePath(String storagePath) {
        // 使用与 uploadFile 相同的逻辑获取绝对路径
        Path uploadPath;
        if (Paths.get(uploadDir).isAbsolute()) {
            uploadPath = Paths.get(uploadDir);
        } else {
            String projectRoot = System.getProperty("user.dir");
            uploadPath = Paths.get(projectRoot, uploadDir);
        }
        return uploadPath.resolve(storagePath).toString();
    }
}

