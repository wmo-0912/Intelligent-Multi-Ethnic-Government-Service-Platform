package com.example.govservice.mapper;

import com.example.govservice.entity.HandlingRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 办理记录数据访问层
 */
@Mapper
public interface HandlingRecordMapper {
    
    /**
     * 根据记录ID查询记录
     */
    @Select("SELECT * FROM HandlingRecord WHERE RecordID = #{recordId}")
    @Results({
            @Result(property = "recordId", column = "RecordID"),
            @Result(property = "userId", column = "UserID"),
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "status", column = "Status"),
            @Result(property = "submitTime", column = "SubmitTime"),
            @Result(property = "handleResult", column = "HandleResult"),
            @Result(property = "handleTime", column = "HandleTime"),
            @Result(property = "handleUserId", column = "HandleUserID"),
            @Result(property = "applicationMaterials", column = "ApplicationMaterials")
    })
    HandlingRecord selectById(@Param("recordId") Integer recordId);
    
    /**
     * 根据用户ID查询申请记录
     */
    @Select("SELECT * FROM HandlingRecord WHERE UserID = #{userId} ORDER BY SubmitTime DESC")
    @Results({
            @Result(property = "recordId", column = "RecordID"),
            @Result(property = "userId", column = "UserID"),
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "status", column = "Status"),
            @Result(property = "submitTime", column = "SubmitTime"),
            @Result(property = "handleResult", column = "HandleResult"),
            @Result(property = "handleTime", column = "HandleTime"),
            @Result(property = "handleUserId", column = "HandleUserID"),
            @Result(property = "applicationMaterials", column = "ApplicationMaterials")
    })
    List<HandlingRecord> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据事项ID查询申请记录
     */
    @Select("SELECT * FROM HandlingRecord WHERE ItemID = #{itemId} ORDER BY SubmitTime DESC")
    @Results({
            @Result(property = "recordId", column = "RecordID"),
            @Result(property = "userId", column = "UserID"),
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "status", column = "Status"),
            @Result(property = "submitTime", column = "SubmitTime"),
            @Result(property = "handleResult", column = "HandleResult"),
            @Result(property = "handleTime", column = "HandleTime"),
            @Result(property = "handleUserId", column = "HandleUserID"),
            @Result(property = "applicationMaterials", column = "ApplicationMaterials")
    })
    List<HandlingRecord> selectByItemId(@Param("itemId") Integer itemId);
    
    /**
     * 查询待处理的申请记录
     */
    @Select("SELECT * FROM HandlingRecord WHERE Status IN ('待处理', '待审核') ORDER BY SubmitTime ASC")
    @Results({
            @Result(property = "recordId", column = "RecordID"),
            @Result(property = "userId", column = "UserID"),
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "status", column = "Status"),
            @Result(property = "submitTime", column = "SubmitTime"),
            @Result(property = "handleResult", column = "HandleResult"),
            @Result(property = "handleTime", column = "HandleTime"),
            @Result(property = "handleUserId", column = "HandleUserID"),
            @Result(property = "applicationMaterials", column = "ApplicationMaterials")
    })
    List<HandlingRecord> selectPendingRecords();
    
    /**
     * 根据状态查询申请记录
     */
    @Select("SELECT * FROM HandlingRecord WHERE Status = #{status} ORDER BY SubmitTime DESC")
    @Results({
            @Result(property = "recordId", column = "RecordID"),
            @Result(property = "userId", column = "UserID"),
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "status", column = "Status"),
            @Result(property = "submitTime", column = "SubmitTime"),
            @Result(property = "handleResult", column = "HandleResult"),
            @Result(property = "handleTime", column = "HandleTime"),
            @Result(property = "handleUserId", column = "HandleUserID"),
            @Result(property = "applicationMaterials", column = "ApplicationMaterials")
    })
    List<HandlingRecord> selectByStatus(@Param("status") String status);
    
    /**
     * 查询所有申请记录
     */
    @Select("SELECT * FROM HandlingRecord ORDER BY SubmitTime DESC")
    @Results({
            @Result(property = "recordId", column = "RecordID"),
            @Result(property = "userId", column = "UserID"),
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "status", column = "Status"),
            @Result(property = "submitTime", column = "SubmitTime"),
            @Result(property = "handleResult", column = "HandleResult"),
            @Result(property = "handleTime", column = "HandleTime"),
            @Result(property = "handleUserId", column = "HandleUserID"),
            @Result(property = "applicationMaterials", column = "ApplicationMaterials")
    })
    List<HandlingRecord> selectAll();
    
    /**
     * 插入申请记录
     */
    @Insert("INSERT INTO HandlingRecord (UserID, ItemID, Status, SubmitTime, ApplicationMaterials) " +
            "VALUES (#{userId}, #{itemId}, #{status}, NOW(), #{applicationMaterials})")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    int insert(HandlingRecord handlingRecord);
    
    /**
     * 更新申请记录
     */
    @Update("UPDATE HandlingRecord SET Status = #{status}, HandleResult = #{handleResult}, " +
            "HandleTime = #{handleTime}, HandleUserID = #{handleUserId}, ApplicationMaterials = #{applicationMaterials} " +
            "WHERE RecordID = #{recordId}")
    int update(HandlingRecord handlingRecord);
    
    /**
     * 更新申请状态
     */
    @Update("UPDATE HandlingRecord SET Status = #{status}, HandleResult = #{handleResult}, " +
            "HandleTime = NOW(), HandleUserID = #{handleUserId} WHERE RecordID = #{recordId}")
    int updateStatus(@Param("recordId") Integer recordId, 
                    @Param("status") String status, 
                    @Param("handleResult") String handleResult,
                    @Param("handleUserId") Integer handleUserId);
    
    /**
     * 根据记录ID删除记录
     */
    @Delete("DELETE FROM HandlingRecord WHERE RecordID = #{recordId}")
    int deleteById(@Param("recordId") Integer recordId);
    
    /**
     * 统计办理量
     */
    @Select("SELECT COUNT(*) FROM HandlingRecord WHERE ItemID = #{itemId}")
    int countByItemId(@Param("itemId") Integer itemId);
    
    /**
     * 统计通过量
     */
    @Select("SELECT COUNT(*) FROM HandlingRecord WHERE ItemID = #{itemId} AND Status = '已通过'")
    int countCompletedByItemId(@Param("itemId") Integer itemId);
}
