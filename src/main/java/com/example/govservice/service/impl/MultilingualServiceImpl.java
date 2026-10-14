package com.example.govservice.service.impl;

import com.example.govservice.entity.Faq;
import com.example.govservice.entity.MultilingualInfo;
import com.example.govservice.exception.BusinessException;
import com.example.govservice.exception.ErrorCode;
import com.example.govservice.mapper.FaqMapper;
import com.example.govservice.mapper.MultilingualMapper;
import com.example.govservice.service.MultilingualService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 多语言服务实现类
 */
@Slf4j
@Service
public class MultilingualServiceImpl implements MultilingualService {
    
    @Autowired
    private MultilingualMapper multilingualMapper;
    
    @Autowired
    private FaqMapper faqMapper;
    
    @Override
    public MultilingualInfo getMultilingualInfo(Integer itemId, String languageType) {
        MultilingualInfo info = multilingualMapper.selectByItemIdAndLanguage(itemId, languageType);
        if (info == null) {
            throw new BusinessException(ErrorCode.TRANSLATION_NOT_FOUND);
        }
        return info;
    }
    
    @Override
    public List<MultilingualInfo> getMultilingualInfoByItemId(Integer itemId) {
        return multilingualMapper.selectByItemId(itemId);
    }
    
    @Override
    public List<MultilingualInfo> getMultilingualInfoByLanguage(String languageType) {
        return multilingualMapper.selectByLanguageType(languageType);
    }
    
    @Override
    public MultilingualInfo createMultilingualInfo(MultilingualInfo multilingualInfo) {
        // 检查翻译是否已存在
        if (checkTranslationExists(multilingualInfo.getItemId(), multilingualInfo.getLanguageType())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "该事项的翻译已存在");
        }
        
        multilingualInfo.setCreateTime(LocalDateTime.now());
        multilingualInfo.setUpdateTime(LocalDateTime.now());
        
        int result = multilingualMapper.insert(multilingualInfo);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "多语言信息创建失败");
        }
        
        log.info("多语言信息创建成功，语言ID：{}", multilingualInfo.getLanguageId());
        return multilingualInfo;
    }
    
    @Override
    public MultilingualInfo updateMultilingualInfo(MultilingualInfo multilingualInfo) {
        // 检查多语言信息是否存在
        MultilingualInfo existingInfo = multilingualMapper.selectById(multilingualInfo.getLanguageId());
        if (existingInfo == null) {
            throw new BusinessException(ErrorCode.TRANSLATION_NOT_FOUND);
        }
        
        multilingualInfo.setUpdateTime(LocalDateTime.now());
        
        int result = multilingualMapper.update(multilingualInfo);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "多语言信息更新失败");
        }
        
        log.info("多语言信息更新成功，语言ID：{}", multilingualInfo.getLanguageId());
        return multilingualInfo;
    }
    
    @Override
    public boolean deleteMultilingualInfo(Integer languageId) {
        // 检查多语言信息是否存在
        MultilingualInfo info = multilingualMapper.selectById(languageId);
        if (info == null) {
            throw new BusinessException(ErrorCode.TRANSLATION_NOT_FOUND);
        }
        
        int result = multilingualMapper.deleteById(languageId);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "多语言信息删除失败");
        }
        
        log.info("多语言信息删除成功，语言ID：{}", languageId);
        return true;
    }
    
    @Override
    public boolean deleteMultilingualInfoByItemId(Integer itemId) {
        int result = multilingualMapper.deleteByItemId(itemId);
        log.info("根据事项ID删除多语言信息成功，事项ID：{}", itemId);
        return result > 0;
    }
    
    @Override
    public boolean checkTranslationExists(Integer itemId, String languageType) {
        return multilingualMapper.checkTranslationExists(itemId, languageType) > 0;
    }
    
    // FAQ相关方法实现
    public Faq getFaqById(Integer questionId) {
        Faq faq = faqMapper.selectById(questionId);
        if (faq == null) {
            throw new BusinessException(ErrorCode.FAQ_NOT_FOUND);
        }
        return faq;
    }
    
    public List<Faq> getFaqsByItemId(Integer itemId) {
        return faqMapper.selectByItemId(itemId);
    }
    
    public List<Faq> getFaqsByItemIdAndLanguage(Integer itemId, String languageType) {
        return faqMapper.selectByItemIdAndLanguage(itemId, languageType);
    }
    
    public List<Faq> getFaqsByLanguage(String languageType) {
        return faqMapper.selectByLanguageType(languageType);
    }
    
    public List<Faq> searchFaqsByKeyword(String keyword, String languageType) {
        if (languageType != null && languageType.trim().isEmpty()) languageType = null;
        try {
            return faqMapper.searchByKeyword(keyword, languageType);
        } catch (Exception e) {
            // 如果XML没有该语句，退化为按语种或全部查询后在内存过滤
            List<Faq> base = languageType != null ? faqMapper.selectByLanguageType(languageType) : faqMapper.selectAll();
            if (keyword == null || keyword.trim().isEmpty()) return base;
            final String kw = keyword.toLowerCase();
            List<Faq> result = new java.util.ArrayList<>();
            for (Faq f : base) {
                if ((f.getQuestion() != null && f.getQuestion().toLowerCase().contains(kw)) ||
                    (f.getAnswer() != null && f.getAnswer().toLowerCase().contains(kw))) {
                    result.add(f);
                }
            }
            return result;
        }
    }
    
    public Faq createFaq(Faq faq) {
        faq.setCreateTime(LocalDateTime.now());
        faq.setUpdateTime(LocalDateTime.now());
        faq.setVisitCount(0);
        
        int result = faqMapper.insert(faq);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "FAQ创建失败");
        }
        
        log.info("FAQ创建成功，问题ID：{}", faq.getQuestionId());
        return faq;
    }
    
    public Faq updateFaq(Faq faq) {
        // 检查FAQ是否存在
        Faq existingFaq = faqMapper.selectById(faq.getQuestionId());
        if (existingFaq == null) {
            throw new BusinessException(ErrorCode.FAQ_NOT_FOUND);
        }
        
        faq.setUpdateTime(LocalDateTime.now());
        
        int result = faqMapper.update(faq);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "FAQ更新失败");
        }
        
        log.info("FAQ更新成功，问题ID：{}", faq.getQuestionId());
        return faq;
    }
    
    public boolean deleteFaq(Integer questionId) {
        // 检查FAQ是否存在
        Faq faq = faqMapper.selectById(questionId);
        if (faq == null) {
            throw new BusinessException(ErrorCode.FAQ_NOT_FOUND);
        }
        
        int result = faqMapper.deleteById(questionId);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "FAQ删除失败");
        }
        
        log.info("FAQ删除成功，问题ID：{}", questionId);
        return true;
    }
    
    public boolean incrementFaqVisitCount(Integer questionId) {
        int result = faqMapper.incrementVisitCount(questionId);
        return result > 0;
    }
    
    public int countFaqVisitByItemId(Integer itemId) {
        return faqMapper.countVisitByItemId(itemId);
    }
    
    public int countFaqVisitByLanguage(String languageType) {
        return faqMapper.countVisitByLanguageType(languageType);
    }
}
