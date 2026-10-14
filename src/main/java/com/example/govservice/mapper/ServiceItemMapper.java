package com.example.govservice.mapper;

import com.example.govservice.entity.ServiceItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 政务事项数据访问层
 */
@Mapper
public interface ServiceItemMapper {
    
    /**
     * 根据事项ID查询事项
     */
    @Select("SELECT * FROM ServiceItem WHERE ItemID = #{itemId}")
    @Results({
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "itemName", column = "ItemName"),
            @Result(property = "departmentName", column = "DepartmentName"),
            @Result(property = "handleRequirement", column = "HandleRequirement"),
            @Result(property = "estimatedDays", column = "EstimatedDays"),
            @Result(property = "isEnabled", column = "IsEnabled"),
            @Result(property = "createTime", column = "CreateTime")
    })
    ServiceItem selectById(@Param("itemId") Integer itemId);
    
    /**
     * 查询所有启用的事项
     */
    @Select("SELECT * FROM ServiceItem WHERE IsEnabled = TRUE ORDER BY CreateTime DESC")
    List<ServiceItem> selectAllEnabled();
    
    /**
     * 查询所有事项
     */
    @Select("SELECT * FROM ServiceItem ORDER BY CreateTime DESC")
    List<ServiceItem> selectAll();
    
    /**
     * 根据部门查询事项
     */
    @Select("SELECT * FROM ServiceItem WHERE DepartmentName = #{departmentName} ORDER BY CreateTime DESC")
    List<ServiceItem> selectByDepartment(@Param("departmentName") String departmentName);
    
    /**
     * 根据部门查询启用的事项
     */
    @Select("SELECT * FROM ServiceItem WHERE DepartmentName = #{departmentName} AND IsEnabled = TRUE ORDER BY CreateTime DESC")
    List<ServiceItem> selectEnabledByDepartment(@Param("departmentName") String departmentName);

    @Select({
            "<script>",
            "SELECT * FROM ServiceItem",
            "<where>",
            "  <if test=\"onlyEnabled != null and onlyEnabled\"> AND IsEnabled = TRUE </if>",
            "  <if test=\"keyword != null and keyword != ''\"> AND (ItemName LIKE CONCAT('%',#{keyword},'%') OR HandleRequirement LIKE CONCAT('%',#{keyword},'%')) </if>",
            "  <if test=\"departmentName != null and departmentName != ''\"> AND DepartmentName = #{departmentName} </if>",
            "</where>",
            "ORDER BY CreateTime DESC",
            "</script>"
    })
    List<ServiceItem> search(@Param("keyword") String keyword,
                             @Param("departmentName") String departmentName,
                             @Param("onlyEnabled") Boolean onlyEnabled);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM ServiceItem",
            "<where>",
            "  <if test=\"onlyEnabled != null and onlyEnabled\"> AND IsEnabled = TRUE </if>",
            "  <if test=\"keyword != null and keyword != ''\"> AND (ItemName LIKE CONCAT('%',#{keyword},'%') OR HandleRequirement LIKE CONCAT('%',#{keyword},'%')) </if>",
            "  <if test=\"departmentName != null and departmentName != ''\"> AND DepartmentName = #{departmentName} </if>",
            "</where>",
            "</script>"
    })
    long countSearch(@Param("keyword") String keyword,
                     @Param("departmentName") String departmentName,
                     @Param("onlyEnabled") Boolean onlyEnabled);

    @Select({
            "<script>",
            "SELECT * FROM ServiceItem",
            "<where>",
            "  <if test=\"onlyEnabled != null and onlyEnabled\"> AND IsEnabled = TRUE </if>",
            "  <if test=\"keyword != null and keyword != ''\"> AND (ItemName LIKE CONCAT('%',#{keyword},'%') OR HandleRequirement LIKE CONCAT('%',#{keyword},'%')) </if>",
            "  <if test=\"departmentName != null and departmentName != ''\"> AND DepartmentName = #{departmentName} </if>",
            "</where>",
            "ORDER BY CreateTime DESC LIMIT #{limit} OFFSET #{offset}",
            "</script>"
    })
    List<ServiceItem> pageSearch(@Param("keyword") String keyword,
                                 @Param("departmentName") String departmentName,
                                 @Param("onlyEnabled") Boolean onlyEnabled,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);
    
    /**
     * 插入事项
     */
    @Insert("INSERT INTO ServiceItem (ItemName, DepartmentName, HandleRequirement, EstimatedDays, IsEnabled, CreateTime) " +
            "VALUES (#{itemName}, #{departmentName}, #{handleRequirement}, #{estimatedDays}, #{isEnabled}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "itemId")
    int insert(ServiceItem serviceItem);
    
    /**
     * 更新事项信息
     */
    @Update("UPDATE ServiceItem SET ItemName = #{itemName}, DepartmentName = #{departmentName}, " +
            "HandleRequirement = #{handleRequirement}, EstimatedDays = #{estimatedDays}, IsEnabled = #{isEnabled} " +
            "WHERE ItemID = #{itemId}")
    int update(ServiceItem serviceItem);
    
    /**
     * 根据事项ID删除事项
     */
    @Delete("DELETE FROM ServiceItem WHERE ItemID = #{itemId}")
    int deleteById(@Param("itemId") Integer itemId);
    
    /**
     * 启用/禁用事项
     */
    @Update("UPDATE ServiceItem SET IsEnabled = #{isEnabled} WHERE ItemID = #{itemId}")
    int updateStatus(@Param("itemId") Integer itemId, @Param("isEnabled") Boolean isEnabled);
}
