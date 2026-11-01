package com.example.govservice.mapper;

import com.example.govservice.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 通知消息Mapper
 */
@Mapper
public interface NotificationMapper {
    
    @Insert("INSERT INTO Notification (UserId, Title, Content, NotifyType, RelatedRecordId, IsRead, CreateTime) " +
            "VALUES (#{userId}, #{title}, #{content}, #{notifyType}, #{relatedRecordId}, FALSE, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "notificationId")
    int insert(Notification notification);
    
    @Select("SELECT * FROM Notification WHERE UserId = #{userId} ORDER BY CreateTime DESC LIMIT #{limit} OFFSET #{offset}")
    @Results({
            @Result(property = "notificationId", column = "NotificationID"),
            @Result(property = "userId", column = "UserId"),
            @Result(property = "title", column = "Title"),
            @Result(property = "content", column = "Content"),
            @Result(property = "notifyType", column = "NotifyType"),
            @Result(property = "relatedRecordId", column = "RelatedRecordId"),
            @Result(property = "isRead", column = "IsRead"),
            @Result(property = "createTime", column = "CreateTime"),
            @Result(property = "readTime", column = "ReadTime")
    })
    List<Notification> selectByUserId(@Param("userId") Integer userId, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM Notification WHERE UserId = #{userId}")
    long countByUserId(@Param("userId") Integer userId);
    
    @Select("SELECT COUNT(*) FROM Notification WHERE UserId = #{userId} AND IsRead = FALSE")
    long countUnreadByUserId(@Param("userId") Integer userId);
    
    @Update("UPDATE Notification SET IsRead = TRUE, ReadTime = NOW() WHERE NotificationID = #{notificationId}")
    int markAsRead(@Param("notificationId") Integer notificationId);
    
    @Update("UPDATE Notification SET IsRead = TRUE, ReadTime = NOW() WHERE UserId = #{userId} AND IsRead = FALSE")
    int markAllAsRead(@Param("userId") Integer userId);
}

