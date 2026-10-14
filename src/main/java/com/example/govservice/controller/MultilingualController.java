package com.example.govservice.controller;

import com.example.govservice.dto.request.LanguageSwitchRequest;
import com.example.govservice.dto.response.CommonResponse;
import com.example.govservice.dto.response.FaqResponse;
import com.example.govservice.entity.Faq;
import com.example.govservice.entity.MultilingualInfo;
import com.example.govservice.service.impl.MultilingualServiceImpl;
import com.example.govservice.util.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 多语言控制器
 */
@Slf4j
@RestController
@RequestMapping("/multilingual")
@Validated
public class MultilingualController {
    
    @Autowired
    private MultilingualServiceImpl multilingualService;
    
    /**
     * 根据事项ID和语种获取多语言信息
     */
    @GetMapping("/item/{itemId}/language/{languageType}")
    public CommonResponse<MultilingualInfo> getMultilingualInfo(@PathVariable Integer itemId,
                                                               @PathVariable String languageType) {
        log.info("获取多语言信息，事项ID：{}，语种：{}", itemId, languageType);
        MultilingualInfo info = multilingualService.getMultilingualInfo(itemId, languageType);
        return CommonResponse.success(info);
    }
    
    /**
     * 根据事项ID获取所有语种的多语言信息
     */
    @GetMapping("/item/{itemId}")
    public CommonResponse<List<MultilingualInfo>> getMultilingualInfoByItemId(@PathVariable Integer itemId) {
        log.info("获取事项的所有多语言信息，事项ID：{}", itemId);
        List<MultilingualInfo> infos = multilingualService.getMultilingualInfoByItemId(itemId);
        return CommonResponse.success(infos);
    }
    
    /**
     * 根据语种获取所有多语言信息
     */
    @GetMapping("/language/{languageType}")
    public CommonResponse<List<MultilingualInfo>> getMultilingualInfoByLanguage(@PathVariable String languageType) {
        log.info("获取语种的所有多语言信息，语种：{}", languageType);
        List<MultilingualInfo> infos = multilingualService.getMultilingualInfoByLanguage(languageType);
        return CommonResponse.success(infos);
    }
    
    /**
     * 创建多语言信息（政务人员功能）
     */
    @PostMapping
    public CommonResponse<MultilingualInfo> createMultilingualInfo(@Valid @RequestBody MultilingualInfo multilingualInfo) {
        log.info("创建多语言信息，事项ID：{}，语种：{}", multilingualInfo.getItemId(), multilingualInfo.getLanguageType());
        MultilingualInfo createdInfo = multilingualService.createMultilingualInfo(multilingualInfo);
        return CommonResponse.success(createdInfo);
    }
    
    /**
     * 更新多语言信息（政务人员功能）
     */
    @PutMapping("/{languageId}")
    public CommonResponse<MultilingualInfo> updateMultilingualInfo(@PathVariable Integer languageId,
                                                                   @Valid @RequestBody MultilingualInfo multilingualInfo) {
        log.info("更新多语言信息，语言ID：{}", languageId);
        multilingualInfo.setLanguageId(languageId);
        MultilingualInfo updatedInfo = multilingualService.updateMultilingualInfo(multilingualInfo);
        return CommonResponse.success(updatedInfo);
    }
    
    /**
     * 删除多语言信息（政务人员功能）
     */
    @DeleteMapping("/{languageId}")
    public CommonResponse<Void> deleteMultilingualInfo(@PathVariable Integer languageId) {
        log.info("删除多语言信息，语言ID：{}", languageId);
        multilingualService.deleteMultilingualInfo(languageId);
        return CommonResponse.success();
    }
    
    /**
     * 根据事项ID删除多语言信息（政务人员功能）
     */
    @DeleteMapping("/item/{itemId}")
    public CommonResponse<Void> deleteMultilingualInfoByItemId(@PathVariable Integer itemId) {
        log.info("根据事项ID删除多语言信息，事项ID：{}", itemId);
        multilingualService.deleteMultilingualInfoByItemId(itemId);
        return CommonResponse.success();
    }
    
    /**
     * 检查翻译是否存在
     */
    @GetMapping("/check/{itemId}/{languageType}")
    public CommonResponse<Boolean> checkTranslationExists(@PathVariable Integer itemId,
                                                        @PathVariable String languageType) {
        log.info("检查翻译是否存在，事项ID：{}，语种：{}", itemId, languageType);
        boolean exists = multilingualService.checkTranslationExists(itemId, languageType);
        return CommonResponse.success(exists);
    }
}

/**
 * FAQ控制器
 */
@Slf4j
@RestController
@RequestMapping("/faq")
@Validated
class FaqController {
    
    @Autowired
    private MultilingualServiceImpl multilingualService;
    
    /**
     * 根据问题ID查询FAQ
     */
    @GetMapping("/{questionId}")
    public CommonResponse<FaqResponse> getFaqById(@PathVariable Integer questionId) {
        log.info("查询FAQ，问题ID：{}", questionId);
        Faq faq = multilingualService.getFaqById(questionId);
        FaqResponse response = convertToResponse(faq);
        return CommonResponse.success(response);
    }
    
    /**
     * 根据事项ID查询FAQ
     */
    @GetMapping("/item/{itemId}")
    public CommonResponse<List<FaqResponse>> getFaqsByItemId(@PathVariable Integer itemId) {
        log.info("根据事项ID查询FAQ，事项ID：{}", itemId);
        List<Faq> faqs = multilingualService.getFaqsByItemId(itemId);
        List<FaqResponse> responses = faqs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return CommonResponse.success(responses);
    }
    
    /**
     * 根据事项ID和语种查询FAQ
     */
    @GetMapping("/item/{itemId}/language/{languageType}")
    public CommonResponse<List<FaqResponse>> getFaqsByItemIdAndLanguage(@PathVariable Integer itemId,
                                                                        @PathVariable String languageType) {
        log.info("根据事项ID和语种查询FAQ，事项ID：{}，语种：{}", itemId, languageType);
        List<Faq> faqs = multilingualService.getFaqsByItemIdAndLanguage(itemId, languageType);
        List<FaqResponse> responses = faqs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return CommonResponse.success(responses);
    }
    
    /**
     * 根据语种查询FAQ
     */
    @GetMapping("/language/{languageType}")
    public CommonResponse<List<FaqResponse>> getFaqsByLanguage(@PathVariable String languageType) {
        log.info("根据语种查询FAQ，语种：{}", languageType);
        List<Faq> faqs = multilingualService.getFaqsByLanguage(languageType);
        List<FaqResponse> responses = faqs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return CommonResponse.success(responses);
    }
    
    /**
     * 根据关键词搜索FAQ
     */
    @GetMapping("/search")
    public CommonResponse<List<FaqResponse>> searchFaqsByKeyword(@RequestParam String keyword,
                                                                @RequestParam(required = false) String languageType) {
        log.info("搜索FAQ，关键词：{}，语种：{}", keyword, languageType);
        List<Faq> faqs = multilingualService.searchFaqsByKeyword(keyword, languageType);
        List<FaqResponse> responses = faqs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return CommonResponse.success(responses);
    }

    /**
     * 机器人问答占位：按关键词匹配FAQ，返回最佳答案；匹配不到则返回多语言兜底文案
     */
    @PostMapping("/bot/ask")
    public CommonResponse<FaqResponse> botAsk(@RequestParam String question,
                                              @RequestParam(required = false) String languageType,
                                              @RequestParam(required = false) Integer itemId) {
        log.info("机器人问答，问题：{}，语种：{}，事项：{}", question, languageType, itemId);
        List<Faq> candidates;
        try {
            if (itemId != null && languageType != null) {
                candidates = multilingualService.getFaqsByItemIdAndLanguage(itemId, languageType);
            } else if (languageType != null) {
                candidates = multilingualService.searchFaqsByKeyword(question, languageType);
            } else {
                candidates = multilingualService.searchFaqsByKeyword(question, null);
            }
        } catch (Exception ex) {
            log.error("FAQ 查询异常: {}", ex.getMessage(), ex);
            candidates = java.util.Collections.emptyList();
        }

        Faq best = null;
        if (candidates != null && !candidates.isEmpty()) {
            // 简单选择：包含度优先
            for (Faq f : candidates) {
                if (f.getQuestion() != null && f.getQuestion().toLowerCase().contains(question.toLowerCase())) {
                    best = f; break;
                }
            }
            if (best == null) best = candidates.get(0);
            try { multilingualService.incrementFaqVisitCount(best.getQuestionId()); } catch (Exception ignored) {}
            return CommonResponse.success(convertToResponse(best));
        }

        // 兜底回答（多语言）
        String lang = languageType == null ? "zh-CN" : languageType;
        String fallback = I18nUtils.getMessage(lang, "faq.not.found", "暂未找到匹配答案，请稍后再试或联系人工服务");
        FaqResponse resp = new FaqResponse(null, itemId, question, fallback, lang, 0, null);
        return CommonResponse.success(resp);
    }
    
    /**
     * 创建FAQ（政务人员功能）
     */
    @PostMapping
    public CommonResponse<FaqResponse> createFaq(@Valid @RequestBody Faq faq) {
        log.info("创建FAQ：{}", faq.getQuestion());
        Faq createdFaq = multilingualService.createFaq(faq);
        FaqResponse response = convertToResponse(createdFaq);
        return CommonResponse.success(response);
    }
    
    /**
     * 更新FAQ（政务人员功能）
     */
    @PutMapping("/{questionId}")
    public CommonResponse<FaqResponse> updateFaq(@PathVariable Integer questionId,
                                                 @Valid @RequestBody Faq faq) {
        log.info("更新FAQ，问题ID：{}", questionId);
        faq.setQuestionId(questionId);
        Faq updatedFaq = multilingualService.updateFaq(faq);
        FaqResponse response = convertToResponse(updatedFaq);
        return CommonResponse.success(response);
    }
    
    /**
     * 删除FAQ（政务人员功能）
     */
    @DeleteMapping("/{questionId}")
    public CommonResponse<Void> deleteFaq(@PathVariable Integer questionId) {
        log.info("删除FAQ，问题ID：{}", questionId);
        multilingualService.deleteFaq(questionId);
        return CommonResponse.success();
    }
    
    /**
     * 增加FAQ访问次数
     */
    @PostMapping("/{questionId}/visit")
    public CommonResponse<Void> incrementFaqVisitCount(@PathVariable Integer questionId) {
        log.info("增加FAQ访问次数，问题ID：{}", questionId);
        multilingualService.incrementFaqVisitCount(questionId);
        return CommonResponse.success();
    }
    
    /**
     * 统计FAQ访问量
     */
    @GetMapping("/statistics/item/{itemId}")
    public CommonResponse<Integer> countFaqVisitByItemId(@PathVariable Integer itemId) {
        log.info("统计FAQ访问量，事项ID：{}", itemId);
        int count = multilingualService.countFaqVisitByItemId(itemId);
        return CommonResponse.success(count);
    }
    
    /**
     * 统计FAQ访问量（按语种）
     */
    @GetMapping("/statistics/language/{languageType}")
    public CommonResponse<Integer> countFaqVisitByLanguage(@PathVariable String languageType) {
        log.info("统计FAQ访问量，语种：{}", languageType);
        int count = multilingualService.countFaqVisitByLanguage(languageType);
        return CommonResponse.success(count);
    }
    
    /**
     * 转换Faq为FaqResponse
     */
    private FaqResponse convertToResponse(Faq faq) {
        FaqResponse response = new FaqResponse();
        BeanUtils.copyProperties(faq, response);
        return response;
    }
}
