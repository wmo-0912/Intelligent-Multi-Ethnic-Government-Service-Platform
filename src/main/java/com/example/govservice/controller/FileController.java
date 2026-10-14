package com.example.govservice.controller;

import com.example.govservice.dto.response.CommonResponse;
import com.example.govservice.entity.FileInfo;
import com.example.govservice.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public CommonResponse<FileInfo> uploadFile(@RequestParam("file") MultipartFile file,
                                               @RequestParam(required = false) Integer relatedRecordId,
                                               @RequestParam(required = false) String fileCategory,
                                               HttpServletRequest request) {
        // 从token获取用户ID
        Object userIdObj = request.getAttribute("jwt.userId");
        Integer userId = userIdObj == null ? null : Integer.valueOf(String.valueOf(userIdObj));
        
        log.info("文件上传请求：fileName={}, userId={}, recordId={}", file.getOriginalFilename(), userId, relatedRecordId);
        
        FileInfo fileInfo = fileStorageService.uploadFile(file, userId, relatedRecordId, fileCategory);
        return CommonResponse.success(fileInfo);
    }
    
    /**
     * 查询文件信息
     */
    @GetMapping("/{fileId}")
    public CommonResponse<FileInfo> getFileInfo(@PathVariable Integer fileId) {
        FileInfo fileInfo = fileStorageService.getFileById(fileId);
        return CommonResponse.success(fileInfo);
    }
    
    /**
     * 根据办理记录ID查询文件列表
     */
    @GetMapping("/record/{recordId}")
    public CommonResponse<List<FileInfo>> getFilesByRecordId(@PathVariable Integer recordId) {
        List<FileInfo> files = fileStorageService.getFilesByRecordId(recordId);
        return CommonResponse.success(files);
    }
    
    /**
     * 查询当前用户上传的文件
     */
    @GetMapping("/user/my")
    public CommonResponse<List<FileInfo>> getMyFiles(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("jwt.userId");
        Integer userId = userIdObj == null ? null : Integer.valueOf(String.valueOf(userIdObj));
        
        List<FileInfo> files = fileStorageService.getFilesByUserId(userId);
        return CommonResponse.success(files);
    }
    
    /**
     * 下载文件
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer fileId) {
        FileInfo fileInfo = fileStorageService.getFileById(fileId);
        String actualPath = fileStorageService.getActualFilePath(fileInfo.getStoragePath());
        
        File file = new File(actualPath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        Resource resource = new FileSystemResource(file);
        
        // 设置文件名编码
        String filename = fileInfo.getFileName();
        try {
            filename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            log.error("文件名编码失败", e);
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
    
    /**
     * 预览文件（针对图片、PDF等）
     */
    @GetMapping("/{fileId}/preview")
    public ResponseEntity<Resource> previewFile(@PathVariable Integer fileId) {
        FileInfo fileInfo = fileStorageService.getFileById(fileId);
        String actualPath = fileStorageService.getActualFilePath(fileInfo.getStoragePath());
        
        File file = new File(actualPath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        Resource resource = new FileSystemResource(file);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }
    
    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    public CommonResponse<Void> deleteFile(@PathVariable Integer fileId) {
        log.info("删除文件：fileId={}", fileId);
        fileStorageService.deleteFile(fileId);
        return CommonResponse.success();
    }
}

