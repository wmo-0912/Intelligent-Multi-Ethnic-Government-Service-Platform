package com.example.govservice.service.impl;

import com.example.govservice.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * AI智能回答服务实现
 * 支持多种主流AI服务：百度文心、阿里通义、OpenAI、智谱AI等
 */
@Slf4j
@Service
public class AiServiceImpl implements AiService {
    
    private final RestTemplate restTemplate;
    
    @Value("${ai.service.enabled:false}")
    private boolean aiServiceEnabled;
    
    @Value("${ai.service.provider:baidu}")
    private String provider;
    
    @Value("${ai.service.api-key:}")
    private String apiKey;
    
    @Value("${ai.service.secret-key:}")
    private String secretKey;
    
    @Value("${ai.service.url:}")
    private String customUrl;
    
    @Value("${ai.service.timeout:30000}")
    private int timeout;
    
    public AiServiceImpl() {
        this.restTemplate = new RestTemplate();
    }
    
    @Override
    public String getAiAnswer(String question, String languageType, String context) throws Exception {
        if (!isAvailable()) {
            log.warn("AI服务未启用或未配置，跳过AI调用");
            return null;
        }
        
        try {
            log.info("调用AI服务[{}]，问题：{}，语种：{}", provider, question, languageType);
            
            String answer = null;
            switch (provider.toLowerCase()) {
                case "baidu":
                    answer = callBaiduWenxin(question, languageType, context);
                    break;
                case "alibaba":
                case "tongyi":
                    answer = callAlibabaTongyi(question, languageType, context);
                    break;
                case "openai":
                    answer = callOpenAI(question, languageType, context);
                    break;
                case "zhipu":
                    answer = callZhipuAI(question, languageType, context);
                    break;
                case "local":
                    // 本地规则引擎，完全免费，无需API Key
                    answer = callLocalRuleEngine(question, languageType, context);
                    break;
                case "custom":
                    answer = callCustomService(question, languageType, context);
                    break;
                default:
                    log.warn("未知的AI服务提供商：{}", provider);
                    return null;
            }
            
            if (answer != null && !answer.trim().isEmpty()) {
                log.info("AI服务返回答案成功，长度：{}", answer.length());
                return answer.trim();
            }
            
        } catch (Exception e) {
            log.error("调用AI服务失败：{}", e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * 调用百度文心一言（千帆大模型平台）
     * 支持v2版本的IAM认证（直接使用API Key）和旧版access_token方式
     */
    private String callBaiduWenxin(String question, String languageType, String context) throws Exception {
        // v2版本：如果API Key以bce-v3开头，说明是IAM认证格式，直接使用
        if (apiKey != null && apiKey.startsWith("bce-v3/")) {
            return callQianfanV2(question, languageType);
        }
        
        // 如果配置了Secret Key，尝试使用旧版access_token方式
        if (secretKey != null && !secretKey.isEmpty()) {
            String accessToken = getBaiduAccessToken();
            if (accessToken != null) {
                return callQianfanWithToken(question, accessToken, languageType);
            }
        }
        
        // 否则尝试直接使用API Key（兼容其他格式）
        return callQianfanV2(question, languageType);
    }
    
    /**
     * 使用access_token调用（旧版方式）
     */
    private String callQianfanWithToken(String question, String accessToken, String languageType) throws Exception {
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" + accessToken;
        
        Map<String, Object> requestBody = new HashMap<>();
        java.util.List<Map<String, String>> messages = new java.util.ArrayList<>();
        
        // 百度文心一言可能不支持system角色，将系统提示词放在用户消息中
        String systemPrompt = getSystemPrompt(languageType);
        // 将提示词和问题组合，但格式要清晰
        String fullQuestion = systemPrompt + "\n\n请回答以下问题：\n" + question;
        
        log.debug("发送给百度的完整消息（前200字符）：{}", 
                 fullQuestion.length() > 200 ? fullQuestion.substring(0, 200) + "..." : fullQuestion);
        
        // 添加用户问题（包含系统提示词）
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", fullQuestion);
        messages.add(userMessage);
        
        requestBody.put("messages", messages);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url, HttpMethod.POST, request,
            new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            if (body.containsKey("result")) {
                return body.get("result").toString();
            }
        }
        return null;
    }
    
    /**
     * 获取政务服务的系统提示词
     */
    private String getSystemPrompt(String languageType) {
        String lang = languageType != null ? languageType : "zh-CN";
        
        // 根据语言返回不同的提示词
        switch (lang) {
            case "zh-CN":
                return "你是多民族地区政务服务平台的智能咨询助手。\n" +
                       "重要提示：当用户说'你好'或打招呼时，你必须回复：'你好，这里是政务服务中心智能回复。有什么可以帮您的吗？'\n" +
                       "你的职责：\n" +
                       "1. 友好、专业地回答政务服务相关问题（身份证、户口迁移、营业执照等）\n" +
                       "2. 回答要准确、简洁、易懂\n" +
                       "3. 不确定时建议咨询人工服务";
            case "ug":
                return "سىز كۆپ مىللەت رايونى ھۆكۈمەت مۇلازىمەت سۇپىسىنىڭ ئەقلىي مەسلىھەت ياردەمچىسى. ھوقۇقىڭىز:\n" +
                       "1. ئىشلەتكۈچىلەرنىڭ ھۆكۈمەت مۇلازىمەت مەسىلىسىگە دائىر سوراقلىرىغا دوستانه ۋە كەسپىي جاۋاب بېرىش\n" +
                       "2. ئىشلەتكۈچى سىلەشكەندە، جاۋاب: 'ساڭا، بۇ يەر ھۆكۈمەت مۇلازىمەت مەركىزىنىڭ ئەقلىي جاۋابى. قانداق ياردەم بېرەلەيمەن؟'\n" +
                       "3. كىملىك كارتا، ئائىلە باشقۇرۇش، تىجارەت ئىجازەت نامى قاتارلىق ھۆكۈمەت مۇلازىمەت مەسىلىلىرىگە ئەھمىيەت بېرىش";
            case "bo":
                return "ཁྱེད་ཀྱིས་མི་རིགས་མང་པོའི་ས་ཁུལ་སྲིད་འཕྲུལ་ཞབས་ཞུའི་སྤྱི་ཚོགས་ཀྱི་རིག་ནུས་ལན་སློང་རོགས་པ་ཡིན།";
            case "mn":
                return "Та бол олон үндэстний бүс нутгийн засаг захиргааны үйлчилгээний платформын оюун ухааны зөвлөх туслах юм.";
            default:
                return "You are an intelligent consultation assistant for the Multi-ethnic Regional Government Service Platform. " +
                       "When users greet you, respond with: 'Hello, this is the intelligent reply from the Government Service Center. How can I help you?' " +
                       "Focus on answering questions about government services such as ID card processing, household registration, business licenses, etc.";
        }
    }
    
    /**
     * 使用v2版本API Key调用（IAM认证）
     */
    private String callQianfanV2(String question, String languageType) throws Exception {
        // 千帆平台v2 API地址 - 兼容OpenAI协议
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions";
        
        Map<String, Object> requestBody = new HashMap<>();
        java.util.List<Map<String, String>> messages = new java.util.ArrayList<>();
        
        // 百度文心一言可能不支持system角色，将系统提示词放在用户消息中
        String systemPrompt = getSystemPrompt(languageType);
        // 将提示词和问题组合，但格式要清晰
        String fullQuestion = systemPrompt + "\n\n请回答以下问题：\n" + question;
        
        log.debug("发送给百度的完整消息（前200字符）：{}", 
                 fullQuestion.length() > 200 ? fullQuestion.substring(0, 200) + "..." : fullQuestion);
        
        // 添加用户问题（包含系统提示词）
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", fullQuestion);
        messages.add(userMessage);
        
        requestBody.put("messages", messages);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // v2版本使用IAM认证，API Key作为Authorization Bearer token
        headers.set("Authorization", "Bearer " + apiKey);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url, HttpMethod.POST, request,
            new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            
            // 百度API可能的响应格式：
            // 1. {"result": "回答内容"}
            // 2. {"choices": [{"message": {"content": "回答内容"}}]}
            // 3. {"output": {"text": "回答内容"}}
            
            String answer = null;
            
            if (body.containsKey("result")) {
                answer = body.get("result").toString();
            } else if (body.containsKey("choices")) {
                Object choices = body.get("choices");
                if (choices instanceof java.util.List && !((java.util.List<?>) choices).isEmpty()) {
                    Object firstChoice = ((java.util.List<?>) choices).get(0);
                    if (firstChoice instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> choiceMap = (Map<String, Object>) firstChoice;
                        if (choiceMap.containsKey("message")) {
                            Object message = choiceMap.get("message");
                            if (message instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> messageMap = (Map<String, Object>) message;
                                if (messageMap.containsKey("content")) {
                                    answer = messageMap.get("content").toString();
                                }
                            }
                        }
                    }
                }
            } else if (body.containsKey("output")) {
                Object output = body.get("output");
                if (output instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> outputMap = (Map<String, Object>) output;
                    if (outputMap.containsKey("text")) {
                        answer = outputMap.get("text").toString();
                    }
                }
            } else if (body.containsKey("error_code") || body.containsKey("error")) {
                // 百度API返回错误
                Object errorCode = body.get("error_code");
                Object errorMsg = body.get("error_msg");
                
                log.error("百度API返回错误：error_code={}, error_msg={}, 完整响应：{}", errorCode, errorMsg, body);
                
                // 特殊处理配额限制错误，返回友好提示
                if (errorCode != null && errorCode.toString().equals("17")) {
                    log.warn("百度API每日请求限额已用完，需要充值或等待次日重置");
                    throw new Exception("AI服务今日调用次数已达上限，请明天再试或联系管理员");
                }
                
                // 其他错误也抛出异常，让上层处理
                throw new Exception("AI服务调用失败：" + (errorMsg != null ? errorMsg.toString() : "未知错误"));
            }
            
            if (answer != null && !answer.trim().isEmpty()) {
                return answer.trim();
            } else {
                log.warn("百度API返回异常，响应体：{}", body);
            }
        } else {
            log.warn("百度API返回非200状态码：{}，响应体：{}", response.getStatusCode(), response.getBody());
        }
        return null;
    }
    
    
    /**
     * 获取百度access_token（旧版方式，需要API Key和Secret Key）
     */
    private String getBaiduAccessToken() {
        if (secretKey == null || secretKey.isEmpty()) {
            log.warn("百度Secret Key未配置，无法获取access_token");
            return null;
        }
        
        try {
            String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" 
                       + URLEncoder.encode(apiKey, StandardCharsets.UTF_8.toString())
                       + "&client_secret=" 
                       + URLEncoder.encode(secretKey, StandardCharsets.UTF_8.toString());
            
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, HttpMethod.POST, request,
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (body.containsKey("access_token")) {
                    return body.get("access_token").toString();
                } else if (body.containsKey("error")) {
                    log.error("获取百度access_token失败：{}", body.get("error_description"));
                }
            }
        } catch (Exception e) {
            log.error("获取百度access_token失败：{}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 调用阿里通义千问
     */
    private String callAlibabaTongyi(String question, String languageType, String context) throws Exception {
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-turbo");
        
        Map<String, Object> input = new HashMap<>();
        input.put("prompt", question);
        requestBody.put("input", input);
        
        requestBody.put("parameters", java.util.Collections.singletonMap("temperature", 0.7));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url, HttpMethod.POST, request,
            new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            if (body.containsKey("output")) {
                Object output = body.get("output");
                if (output instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> outputMap = (Map<String, Object>) output;
                    if (outputMap.containsKey("text")) {
                        return outputMap.get("text").toString();
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 调用OpenAI ChatGPT
     */
    private String callOpenAI(String question, String languageType, String context) throws Exception {
        String url = "https://api.openai.com/v1/chat/completions";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        
        java.util.List<Map<String, String>> messages = new java.util.ArrayList<>();
        messages.add(java.util.Collections.singletonMap("role", "user"));
        messages.get(0).put("content", question);
        requestBody.put("messages", messages);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url, HttpMethod.POST, request,
            new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            if (body.containsKey("choices")) {
                Object choices = body.get("choices");
                if (choices instanceof java.util.List && !((java.util.List<?>) choices).isEmpty()) {
                    Object firstChoice = ((java.util.List<?>) choices).get(0);
                    if (firstChoice instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> choiceMap = (Map<String, Object>) firstChoice;
                        if (choiceMap.containsKey("message")) {
                            Object message = choiceMap.get("message");
                            if (message instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> messageMap = (Map<String, Object>) message;
                                if (messageMap.containsKey("content")) {
                                    return messageMap.get("content").toString();
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 调用智谱AI（每天3000次免费额度）
     */
    private String callZhipuAI(String question, String languageType, String context) throws Exception {
        String url = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
        
        Map<String, Object> requestBody = new HashMap<>();
        // 使用glm-4-flash版本，响应更快，适合对话场景
        requestBody.put("model", "glm-4-flash");
        
        java.util.List<Map<String, String>> messages = new java.util.ArrayList<>();
        
        // 添加系统提示词，让AI知道自己的身份
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", getSystemPrompt(languageType));
        messages.add(systemMessage);
        
        // 添加用户问题
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", question);
        messages.add(userMessage);
        
        requestBody.put("messages", messages);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url, HttpMethod.POST, request,
            new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            
            // 智谱AI响应格式：{"choices": [{"message": {"content": "回答内容"}}]}
            if (body.containsKey("choices")) {
                Object choices = body.get("choices");
                if (choices instanceof java.util.List && !((java.util.List<?>) choices).isEmpty()) {
                    Object firstChoice = ((java.util.List<?>) choices).get(0);
                    if (firstChoice instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> choiceMap = (Map<String, Object>) firstChoice;
                        if (choiceMap.containsKey("message")) {
                            Object messageObj = choiceMap.get("message");
                            if (messageObj instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> messageMap = (Map<String, Object>) messageObj;
                                if (messageMap.containsKey("content")) {
                                    return messageMap.get("content").toString();
                                }
                            }
                        }
                    }
                }
            } else if (body.containsKey("error")) {
                // 处理错误响应
                log.error("智谱AI返回错误：{}", body);
                Object errorCode = body.get("error_code");
                if (errorCode != null) {
                    String errorCodeStr = errorCode.toString();
                    if (errorCodeStr.equals("1301")) {
                        throw new Exception("智谱AI今日调用次数已达上限，请明天再试");
                    } else {
                        Object errorMsg = body.get("error_msg");
                        throw new Exception("智谱AI调用失败：" + (errorMsg != null ? errorMsg.toString() : errorCodeStr));
                    }
                }
            }
        } else {
            log.warn("智谱AI返回非200状态码：{}，响应：{}", response.getStatusCode(), response.getBody());
        }
        return null;
    }
    
    /**
     * 调用自定义服务
     */
    private String callCustomService(String question, String languageType, String context) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("question", question);
        requestBody.put("languageType", languageType != null ? languageType : "zh-CN");
        if (context != null && !context.isEmpty()) {
            requestBody.put("context", context);
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.set("Authorization", "Bearer " + apiKey);
        }
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            customUrl, HttpMethod.POST, request,
            new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            // 尝试多种响应格式
            if (body.containsKey("answer")) {
                return body.get("answer").toString();
            } else if (body.containsKey("data")) {
                Object data = body.get("data");
                if (data instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> dataMap = (Map<String, Object>) data;
                    if (dataMap.containsKey("answer")) {
                        return dataMap.get("answer").toString();
                    } else if (dataMap.containsKey("content")) {
                        return dataMap.get("content").toString();
                    }
                } else if (data instanceof String) {
                    return (String) data;
                }
            } else if (body.containsKey("content")) {
                return body.get("content").toString();
            }
        }
        return null;
    }
    
    /**
     * 本地规则引擎（完全免费，无需API）
     * 基于关键词匹配和规则返回智能回答
     */
    private String callLocalRuleEngine(String question, String languageType, String context) {
        // 本地规则引擎当前支持中文，后续可扩展多语言
        String q = question.toLowerCase().trim();
        
        // 打招呼处理
        if (q.contains("你好") || q.contains("hello") || q.equals("hi") || q.equals("嗨") || 
            q.contains("您好") || q.contains("在吗") || q.contains("有人吗")) {
            return "你好，这里是政务服务中心智能回复。有什么可以帮您的吗？";
        }
        
        // 感谢/再见处理
        if (q.contains("谢谢") || q.contains("感谢") || q.contains("再见") || q.contains("拜拜")) {
            return "不客气！很高兴能为您服务。如果还有其他问题，随时欢迎咨询。祝您生活愉快！";
        }
        
        // 政务服务关键词匹配（更全面的关键词覆盖）
        Map<String, String> serviceKeywords = new HashMap<>();
        
        // 身份证相关
        serviceKeywords.put("身份证", "办理身份证需要准备以下材料：\n1. 本人身份证原件及复印件\n2. 户口本原件及复印件\n3. 近期免冠照片2张\n4. 填写申请表\n\n办理时间通常为15个工作日。如需加急办理，请咨询当地派出所。");
        serviceKeywords.put("身份证办理", serviceKeywords.get("身份证"));
        serviceKeywords.put("办理身份证", serviceKeywords.get("身份证"));
        
        // 户口相关
        serviceKeywords.put("户口", "户口迁移需要以下材料：\n1. 户口迁移证明\n2. 身份证原件及复印件\n3. 房产证或租赁合同\n4. 填写迁移申请表\n\n具体流程和所需材料可能因地区而异，建议咨询当地派出所。");
        serviceKeywords.put("户口迁移", serviceKeywords.get("户口"));
        serviceKeywords.put("迁户口", serviceKeywords.get("户口"));
        
        // 营业执照相关
        serviceKeywords.put("营业执照", "办理营业执照需要准备：\n1. 企业名称预先核准通知书\n2. 法定代表人身份证原件及复印件\n3. 公司章程\n4. 经营场所证明（房产证或租赁合同）\n\n办理时间约5-10个工作日。建议提前准备好所有材料，以免影响办理进度。");
        serviceKeywords.put("办营业执照", serviceKeywords.get("营业执照"));
        serviceKeywords.put("企业注册", serviceKeywords.get("营业执照"));
        
        // 社保相关
        serviceKeywords.put("社保", "社保相关业务：\n1. 查询缴费记录 - 携带身份证到社保局或登录官网查询\n2. 办理社保卡 - 携带身份证和照片到指定网点办理\n3. 咨询缴费标准 - 可拨打社保热线12333或到窗口咨询\n\n如有具体问题，建议到当地社保局窗口详细咨询。");
        serviceKeywords.put("社会保险", serviceKeywords.get("社保"));
        serviceKeywords.put("社保卡", "办理社保卡需要：携带身份证原件和1寸免冠照片到当地社保卡服务网点办理，一般7-15个工作日可领取。");
        
        // 公积金相关
        serviceKeywords.put("公积金", "公积金业务：\n1. 查询余额 - 登录当地公积金官网或APP查询\n2. 提取公积金 - 需提供相关证明材料（购房、租房、还贷等）\n3. 贷款申请 - 需满足连续缴存6个月以上且账户余额充足\n\n具体办理流程请咨询当地公积金管理中心。");
        serviceKeywords.put("住房公积金", serviceKeywords.get("公积金"));
        
        // 护照相关
        serviceKeywords.put("护照", "办理护照需要：\n1. 身份证原件及复印件\n2. 户口本原件及复印件\n3. 近期免冠照片\n4. 填写申请表\n\n首次办理护照一般需要15个工作日。请提前准备好材料到出入境管理部门办理。");
        serviceKeywords.put("办护照", serviceKeywords.get("护照"));
        
        // 结婚证相关
        serviceKeywords.put("结婚", "办理结婚登记需要：\n1. 双方身份证原件\n2. 双方户口本原件\n3. 3张2寸近期合影照片\n4. 双方到场\n\n到一方户籍所在地的民政局婚姻登记处办理，当天即可领取。");
        serviceKeywords.put("结婚证", serviceKeywords.get("结婚"));
        serviceKeywords.put("登记结婚", serviceKeywords.get("结婚"));
        
        // 关键词匹配（优先精确匹配，再模糊匹配）
        for (Map.Entry<String, String> entry : serviceKeywords.entrySet()) {
            if (q.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // 通用回复
        return "感谢您的咨询。关于政务服务相关问题，建议您：\n\n" +
               "1. 详细描述您的具体需求，我可以为您提供更精准的回答\n" +
               "2. 查看平台上的常见问题（FAQ），快速找到答案\n" +
               "3. 如需人工服务，可拨打政务服务热线\n" +
               "4. 或前往就近的政务服务中心窗口咨询\n\n" +
               "我们致力于为您提供优质的政务服务，祝您办事顺利！";
    }
    
    @Override
    public boolean isAvailable() {
        if (!aiServiceEnabled) {
            return false;
        }
        
        // 本地规则引擎不需要API Key
        if ("local".equals(provider)) {
            return true;
        }
        
        if ("custom".equals(provider)) {
            return customUrl != null && !customUrl.trim().isEmpty();
        } else {
            return apiKey != null && !apiKey.trim().isEmpty();
        }
    }
}
