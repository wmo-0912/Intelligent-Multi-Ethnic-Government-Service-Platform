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
    Faq selectById(@Param("questionId") Integer questionId);
    
    /**
     * 根据事项ID查询FAQ
     */
    List<Faq> selectByItemId(@Param("itemId") Integer itemId);
    
    /**
     * 根据事项ID和语种查询FAQ
     */
    List<Faq> selectByItemIdAndLanguage(@Param("itemId") Integer itemId, 
                                       @Param("languageType") String languageType);
    
    /**
     * 根据语种查询FAQ
     */
    List<Faq> selectByLanguageType(@Param("languageType") String languageType);
    
    /**
     * 根据关键词搜索FAQ
     */
    @Select({
            "<script>",
            "SELECT * FROM FAQ",
            "<where>",
            "  <if test=\"languageType != null and languageType != ''\"> AND LanguageType = #{languageType} </if>",
            "  <if test=\"keyword != null and keyword != ''\">",
            "    AND (Question LIKE CONCAT('%', #{keyword}, '%') OR Answer LIKE CONCAT('%', #{keyword}, '%'))",
            "  </if>",
            "</where>",
            "ORDER BY CreateTime DESC",
            "</script>"
    })
    List<Faq> searchByKeyword(@Param("keyword") String keyword, 
                              @Param("languageType") String languageType);
    
    /**
     * 查询所有FAQ
     */
    List<Faq> selectAll();
    
    /**
     * 插入FAQ
     */
    int insert(Faq faq);
    
    /**
     * 更新FAQ
     */
    int update(Faq faq);
    
    /**
     * 根据问题ID删除FAQ
     */
    int deleteById(@Param("questionId") Integer questionId);
    
    /**
     * 根据事项ID删除FAQ
     */
    int deleteByItemId(@Param("itemId") Integer itemId);
    
    /**
     * 增加访问次数
     */
    int incrementVisitCount(@Param("questionId") Integer questionId);
    
    /**
     * 统计FAQ访问量
     */
    int countVisitByItemId(@Param("itemId") Integer itemId);
    
    /**
     * 统计FAQ访问量（按语种）
     */
    int countVisitByLanguageType(@Param("languageType") String languageType);
}
