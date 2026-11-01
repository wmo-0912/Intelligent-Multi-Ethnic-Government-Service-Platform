package com.example.govservice.mapper;

import com.example.govservice.entity.FileInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 文件信息Mapper
 */
@Mapper
public interface FileInfoMapper {
    
    @Insert("INSERT INTO FileInfo (FileName, StoragePath, FileType, FileSize, UploadUserId, RelatedRecordId, FileCategory, UploadTime, Deleted) " +
            "VALUES (#{fileName}, #{storagePath}, #{fileType}, #{fileSize}, #{uploadUserId}, #{relatedRecordId}, #{fileCategory}, NOW(), FALSE)")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insert(FileInfo fileInfo);
    
    @Select("SELECT * FROM FileInfo WHERE FileID = #{fileId} AND Deleted = FALSE")
    @Results({
            @Result(property = "fileId", column = "FileID"),
            @Result(property = "fileName", column = "FileName"),
            @Result(property = "storagePath", column = "StoragePath"),
            @Result(property = "fileType", column = "FileType"),
            @Result(property = "fileSize", column = "FileSize"),
            @Result(property = "uploadUserId", column = "UploadUserId"),
            @Result(property = "relatedRecordId", column = "RelatedRecordId"),
            @Result(property = "fileCategory", column = "FileCategory"),
            @Result(property = "uploadTime", column = "UploadTime"),
            @Result(property = "deleted", column = "Deleted")
    })
    FileInfo selectById(@Param("fileId") Integer fileId);
    
    @Select("SELECT * FROM FileInfo WHERE RelatedRecordId = #{recordId} AND Deleted = FALSE ORDER BY UploadTime DESC")
    @Results({
            @Result(property = "fileId", column = "FileID"),
            @Result(property = "fileName", column = "FileName"),
            @Result(property = "storagePath", column = "StoragePath"),
            @Result(property = "fileType", column = "FileType"),
            @Result(property = "fileSize", column = "FileSize"),
            @Result(property = "uploadUserId", column = "UploadUserId"),
            @Result(property = "relatedRecordId", column = "RelatedRecordId"),
            @Result(property = "fileCategory", column = "FileCategory"),
            @Result(property = "uploadTime", column = "UploadTime"),
            @Result(property = "deleted", column = "Deleted")
    })
    List<FileInfo> selectByRecordId(@Param("recordId") Integer recordId);
    
    @Select("SELECT * FROM FileInfo WHERE UploadUserId = #{userId} AND Deleted = FALSE ORDER BY UploadTime DESC")
    @Results({
            @Result(property = "fileId", column = "FileID"),
            @Result(property = "fileName", column = "FileName"),
            @Result(property = "storagePath", column = "StoragePath"),
            @Result(property = "fileType", column = "FileType"),
            @Result(property = "fileSize", column = "FileSize"),
            @Result(property = "uploadUserId", column = "UploadUserId"),
            @Result(property = "relatedRecordId", column = "RelatedRecordId"),
            @Result(property = "fileCategory", column = "FileCategory"),
            @Result(property = "uploadTime", column = "UploadTime"),
            @Result(property = "deleted", column = "Deleted")
    })
    List<FileInfo> selectByUserId(@Param("userId") Integer userId);
    
    @Update("UPDATE FileInfo SET Deleted = TRUE WHERE FileID = #{fileId}")
    int logicDelete(@Param("fileId") Integer fileId);
    
    /**
     * 更新文件的关联记录ID
     */
    @Update("UPDATE FileInfo SET RelatedRecordId = #{recordId} WHERE FileID = #{fileId}")
    int updateRelatedRecordId(@Param("fileId") Integer fileId, @Param("recordId") Integer recordId);
}

