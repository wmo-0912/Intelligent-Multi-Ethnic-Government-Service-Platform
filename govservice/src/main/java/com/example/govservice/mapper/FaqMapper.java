package com.example.govservice.mapper;

import com.example.govservice.entity.Faq;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * FAQ数据访问层
 */
@Mapper
public interface FaqMapper {
    
    /**
     * 根据问题ID查询FAQ
     */
    @Select("SELECT * FROM FAQ WHERE QuestionID = #{questionId}")
    Faq selectById(@Param("questionId") Integer questionId);
    
    /**
     * 根据事项ID查询FAQ
     */
    @Select("SELECT * FROM FAQ WHERE ItemID = #{itemId} ORDER BY CreateTime DESC")
    List<Faq> selectByItemId(@Param("itemId") Integer itemId);
    
    /**
     * 根据事项ID和语种查询FAQ
     */
    @Select("SELECT * FROM FAQ WHERE ItemID = #{itemId} AND LanguageType = #{languageType} ORDER BY CreateTime DESC")
    List<Faq> selectByItemIdAndLanguage(@Param("itemId") Integer itemId, 
                                       @Param("languageType") String languageType);
    
    /**
     * 根据语种查询FAQ
     */
    @Select("SELECT * FROM FAQ WHERE LanguageType = #{languageType} ORDER BY CreateTime DESC")
    List<Faq> selectByLanguageType(@Param("languageType") String languageType);
    
    /**
     * 根据关键词搜索FAQ
     */
    //noinspection SqlDialectInspection,SqlNoDataSourceInspection
    @Select("<script>" +
            "SELECT * FROM FAQ " +
            "<where>" +
            "  <if test='languageType != null and languageType != \"\"'> AND LanguageType = #{languageType} </if>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND (Question LIKE CONCAT('%', #{keyword}, '%') OR Answer LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "</where>" +
            " ORDER BY CreateTime DESC" +
            "</script>")
    List<Faq> searchByKeyword(@Param("keyword") String keyword,
                              @Param("languageType") String languageType);
    
    /**
     * 查询所有FAQ
     */
    @Select("SELECT * FROM FAQ ORDER BY CreateTime DESC")
    List<Faq> selectAll();
    
    /**
     * 插入FAQ
     */
    @Insert("INSERT INTO FAQ (ItemID, Question, Answer, LanguageType, VisitCount, CreateTime) " +
            "VALUES (#{itemId}, #{question}, #{answer}, #{languageType}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "questionId", keyColumn = "QuestionID")
    int insert(Faq faq);
    
    /**
     * 更新FAQ
     */
    @Update("UPDATE FAQ SET ItemID = #{itemId}, Question = #{question}, Answer = #{answer}, " +
            "LanguageType = #{languageType} WHERE QuestionID = #{questionId}")
    int update(Faq faq);
    
    /**
     * 根据问题ID删除FAQ
     */
    @Delete("DELETE FROM FAQ WHERE QuestionID = #{questionId}")
    int deleteById(@Param("questionId") Integer questionId);
    
    /**
     * 根据事项ID删除FAQ
     */
    @Delete("DELETE FROM FAQ WHERE ItemID = #{itemId}")
    int deleteByItemId(@Param("itemId") Integer itemId);
    
    /**
     * 增加访问次数
     */
    @Update("UPDATE FAQ SET VisitCount = VisitCount + 1 WHERE QuestionID = #{questionId}")
    int incrementVisitCount(@Param("questionId") Integer questionId);
    
    /**
     * 统计FAQ访问量
     */
    @Select("SELECT COALESCE(SUM(VisitCount), 0) FROM FAQ WHERE ItemID = #{itemId}")
    int countVisitByItemId(@Param("itemId") Integer itemId);
    
    /**
     * 统计FAQ访问量（按语种）
     */
    @Select("SELECT COALESCE(SUM(VisitCount), 0) FROM FAQ WHERE LanguageType = #{languageType}")
    int countVisitByLanguageType(@Param("languageType") String languageType);
}
