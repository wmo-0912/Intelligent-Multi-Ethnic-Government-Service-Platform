package com.example.govservice.service;

import com.example.govservice.dto.request.LanguageSwitchRequest;
import com.example.govservice.entity.Faq;
import com.example.govservice.entity.MultilingualInfo;

import java.util.List;

/**
 * 多语言服务接口
 */
public interface MultilingualService {
    
    /**
     * 根据事项ID和语种获取多语言信息
     */
    MultilingualInfo getMultilingualInfo(Integer itemId, String languageType);
    
    /**
     * 根据事项ID获取所有语种的多语言信息
     */
    List<MultilingualInfo> getMultilingualInfoByItemId(Integer itemId);
    
    /**
     * 根据语种获取所有多语言信息
     */
    List<MultilingualInfo> getMultilingualInfoByLanguage(String languageType);
    
    /**
     * 创建多语言信息
     */
    MultilingualInfo createMultilingualInfo(MultilingualInfo multilingualInfo);
    
    /**
     * 更新多语言信息
     */
    MultilingualInfo updateMultilingualInfo(MultilingualInfo multilingualInfo);
    
    /**
     * 删除多语言信息
     */
    boolean deleteMultilingualInfo(Integer languageId);
    
    /**
     * 根据事项ID删除多语言信息
     */
    boolean deleteMultilingualInfoByItemId(Integer itemId);
    
    /**
     * 检查翻译是否存在
     */
    boolean checkTranslationExists(Integer itemId, String languageType);
}

/**
 * FAQ服务接口
 */
interface FaqService {
    
    /**
     * 根据问题ID查询FAQ
     */
    Faq getFaqById(Integer questionId);
    
    /**
     * 根据事项ID查询FAQ
     */
    List<Faq> getFaqsByItemId(Integer itemId);
    
    /**
     * 根据事项ID和语种查询FAQ
     */
    List<Faq> getFaqsByItemIdAndLanguage(Integer itemId, String languageType);
    
    /**
     * 根据语种查询FAQ
     */
    List<Faq> getFaqsByLanguage(String languageType);
    
    /**
     * 根据关键词搜索FAQ
     */
    List<Faq> searchFaqsByKeyword(String keyword, String languageType);
    
    /**
     * 创建FAQ
     */
    Faq createFaq(Faq faq);
    
    /**
     * 更新FAQ
     */
    Faq updateFaq(Faq faq);
    
    /**
     * 删除FAQ
     */
    boolean deleteFaq(Integer questionId);
    
    /**
     * 增加FAQ访问次数
     */
    boolean incrementFaqVisitCount(Integer questionId);
    
    /**
     * 统计FAQ访问量
     */
    int countFaqVisitByItemId(Integer itemId);
    
    /**
     * 统计FAQ访问量（按语种）
     */
    int countFaqVisitByLanguage(String languageType);
}
