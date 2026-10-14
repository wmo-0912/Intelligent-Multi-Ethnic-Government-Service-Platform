package com.example.govservice.mapper;

import com.example.govservice.entity.MultilingualInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 多语言数据访问层
 */
@Mapper
public interface MultilingualMapper {
    
    /**
     * 根据语言ID查询多语言信息
     */
    @Select("SELECT * FROM MultilingualInfo WHERE LanguageID = #{languageId}")
    @Results({
            @Result(property = "languageId", column = "LanguageID"),
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "languageType", column = "LanguageType"),
            @Result(property = "itemNameTrans", column = "ItemNameTrans"),
            @Result(property = "requirementTrans", column = "RequirementTrans"),
            @Result(property = "createTime", column = "CreateTime"),
            @Result(property = "updateTime", column = "UpdateTime")
    })
    MultilingualInfo selectById(@Param("languageId") Integer languageId);
    
    /**
     * 根据事项ID和语种查询多语言信息
     */
    @Select("SELECT * FROM MultilingualInfo WHERE ItemID = #{itemId} AND LanguageType = #{languageType}")
    @Results({
            @Result(property = "languageId", column = "LanguageID"),
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "languageType", column = "LanguageType"),
            @Result(property = "itemNameTrans", column = "ItemNameTrans"),
            @Result(property = "requirementTrans", column = "RequirementTrans"),
            @Result(property = "createTime", column = "CreateTime"),
            @Result(property = "updateTime", column = "UpdateTime")
    })
    MultilingualInfo selectByItemIdAndLanguage(@Param("itemId") Integer itemId, 
                                             @Param("languageType") String languageType);
    
    /**
     * 根据事项ID查询所有语种的多语言信息
     */
    @Select("SELECT * FROM MultilingualInfo WHERE ItemID = #{itemId}")
    @Results({
            @Result(property = "languageId", column = "LanguageID"),
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "languageType", column = "LanguageType"),
            @Result(property = "itemNameTrans", column = "ItemNameTrans"),
            @Result(property = "requirementTrans", column = "RequirementTrans"),
            @Result(property = "createTime", column = "CreateTime"),
            @Result(property = "updateTime", column = "UpdateTime")
    })
    List<MultilingualInfo> selectByItemId(@Param("itemId") Integer itemId);
    
    /**
     * 根据语种查询所有多语言信息
     */
    @Select("SELECT * FROM MultilingualInfo WHERE LanguageType = #{languageType}")
    @Results({
            @Result(property = "languageId", column = "LanguageID"),
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "languageType", column = "LanguageType"),
            @Result(property = "itemNameTrans", column = "ItemNameTrans"),
            @Result(property = "requirementTrans", column = "RequirementTrans"),
            @Result(property = "createTime", column = "CreateTime"),
            @Result(property = "updateTime", column = "UpdateTime")
    })
    List<MultilingualInfo> selectByLanguageType(@Param("languageType") String languageType);
    
    /**
     * 查询所有多语言信息
     */
    @Select("SELECT * FROM MultilingualInfo ORDER BY ItemID, LanguageType")
    @Results({
            @Result(property = "languageId", column = "LanguageID"),
            @Result(property = "itemId", column = "ItemID"),
            @Result(property = "languageType", column = "LanguageType"),
            @Result(property = "itemNameTrans", column = "ItemNameTrans"),
            @Result(property = "requirementTrans", column = "RequirementTrans"),
            @Result(property = "createTime", column = "CreateTime"),
            @Result(property = "updateTime", column = "UpdateTime")
    })
    List<MultilingualInfo> selectAll();
    
    /**
     * 插入多语言信息
     */
    @Insert("INSERT INTO MultilingualInfo (ItemID, LanguageType, ItemNameTrans, RequirementTrans, CreateTime, UpdateTime) " +
            "VALUES (#{itemId}, #{languageType}, #{itemNameTrans}, #{requirementTrans}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "languageId")
    int insert(MultilingualInfo multilingualInfo);
    
    /**
     * 更新多语言信息
     */
    @Update("UPDATE MultilingualInfo SET ItemNameTrans = #{itemNameTrans}, RequirementTrans = #{requirementTrans}, " +
            "UpdateTime = #{updateTime} WHERE LanguageID = #{languageId}")
    int update(MultilingualInfo multilingualInfo);
    
    /**
     * 根据语言ID删除多语言信息
     */
    @Delete("DELETE FROM MultilingualInfo WHERE LanguageID = #{languageId}")
    int deleteById(@Param("languageId") Integer languageId);
    
    /**
     * 根据事项ID删除多语言信息
     */
    @Delete("DELETE FROM MultilingualInfo WHERE ItemID = #{itemId}")
    int deleteByItemId(@Param("itemId") Integer itemId);
    
    /**
     * 检查事项和语种的翻译是否存在
     */
    @Select("SELECT COUNT(*) FROM MultilingualInfo WHERE ItemID = #{itemId} AND LanguageType = #{languageType}")
    int checkTranslationExists(@Param("itemId") Integer itemId, 
                              @Param("languageType") String languageType);
}
