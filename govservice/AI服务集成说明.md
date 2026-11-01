# AI智能回答服务集成说明

## 概述

本系统已集成AI智能回答功能，当FAQ（常见问题）找不到匹配答案时，系统会自动调用AI服务生成智能回答，提升用户体验。

## 工作流程

1. **用户提问** → 系统首先在FAQ数据库中搜索匹配的答案
2. **找到FAQ匹配** → 直接返回FAQ中的固定答案
3. **未找到FAQ匹配** → 调用AI服务生成智能回答
4. **AI服务不可用** → 使用多语言兜底文案

## 配置说明

### 1. 配置文件位置

编辑 `src/main/resources/application.yml` 文件，找到以下配置：

```yaml
# AI智能回答服务配置
ai:
  service:
    enabled: false  # 是否启用AI服务，设置为true启用
    url: http://localhost:8080/api/ai/chat  # AI服务API地址，请替换为您的AI服务地址
    api-key: ""  # AI服务的API密钥（如果需要），留空则不需要认证
    timeout: 5000  # 请求超时时间（毫秒）
```

### 2. 配置参数说明

| 参数 | 类型 | 说明 | 必填 |
|------|------|------|------|
| `enabled` | boolean | 是否启用AI服务 | 是 |
| `url` | string | AI服务的HTTP API地址 | 是（启用时） |
| `api-key` | string | API密钥，如果AI服务需要认证 | 否 |
| `timeout` | int | 请求超时时间（毫秒），默认5000 | 否 |

### 3. 启用AI服务

将 `ai.service.enabled` 设置为 `true`，并配置正确的 `url`：

```yaml
ai:
  service:
    enabled: true
    url: http://your-ai-service.com/api/chat
    api-key: "your-api-key-here"  # 如果需要
```

## AI服务API规范

### 请求格式

系统会向AI服务发送POST请求，请求体格式：

```json
{
  "question": "用户的问题",
  "languageType": "zh-CN",
  "context": "事项ID: 1; 相关FAQ数量: 3"
}
```

**请求参数：**
- `question` (string): 用户的问题
- `languageType` (string): 语言类型，如 `zh-CN`、`ug`、`bo`、`mn` 等
- `context` (string, 可选): 上下文信息，包括事项ID和相关FAQ数量

**请求头：**
- `Content-Type: application/json`
- `Authorization: Bearer {api-key}` (如果配置了api-key)

### 响应格式

AI服务应返回以下格式的JSON响应（支持多种格式）：

**格式1：**
```json
{
  "answer": "AI生成的智能回答"
}
```

**格式2：**
```json
{
  "data": {
    "answer": "AI生成的智能回答"
  }
}
```

**格式3：**
```json
{
  "data": {
    "content": "AI生成的智能回答"
  }
}
```

**格式4：**
```json
{
  "content": "AI生成的智能回答"
}
```

**格式5：**
```json
{
  "message": "AI生成的智能回答"
}
```

系统会自动识别以上任意一种格式，并提取答案内容。

### HTTP状态码

- `200 OK`: 成功返回答案
- 其他状态码: 系统会记录错误日志，并使用兜底文案

## 代码实现

### 核心类

1. **AiService** (`com.example.govservice.service.AiService`)
   - AI服务接口

2. **AiServiceImpl** (`com.example.govservice.service.impl.AiServiceImpl`)
   - AI服务实现类，负责调用AI服务API

3. **FaqController.botAsk()** (`com.example.govservice.controller.MultilingualController`)
   - FAQ机器人问答接口，集成了AI服务调用逻辑

### 调用流程

```
FaqController.botAsk()
  ↓
搜索FAQ匹配
  ↓ (未找到匹配)
AiService.getAiAnswer()
  ↓
RestTemplate发送HTTP请求
  ↓
解析AI服务响应
  ↓ (成功)
返回AI生成的答案
  ↓ (失败)
返回兜底文案
```

## 测试建议

### 1. 测试AI服务连接

确保AI服务URL正确且可访问：

```bash
curl -X POST http://your-ai-service.com/api/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-api-key" \
  -d '{
    "question": "如何办理身份证？",
    "languageType": "zh-CN"
  }'
```

### 2. 测试集成功能

1. 启动后端服务
2. 确保配置文件中 `ai.service.enabled=true`
3. 调用 `/api/faq/bot/ask` 接口，使用一个FAQ中不存在的问题
4. 检查日志，确认是否调用了AI服务
5. 验证返回的答案是否为AI生成的内容

### 3. 日志查看

系统会记录以下日志：

- `调用AI服务，问题：...，语种：...` - AI服务调用开始
- `AI服务返回答案成功，长度：...` - AI服务调用成功
- `AI服务未返回有效答案，使用兜底文案` - AI服务返回空答案
- `调用AI服务失败：...` - AI服务调用失败

## 故障排除

### 问题1：AI服务未启用

**现象：** 系统使用兜底文案，日志显示 "AI服务未启用或不可用"

**解决：** 检查配置文件，确保 `ai.service.enabled=true`

### 问题2：连接超时

**现象：** 日志显示连接超时错误

**解决：**
- 检查AI服务URL是否正确
- 检查网络连接
- 增加 `timeout` 配置值

### 问题3：认证失败

**现象：** 返回401或403错误

**解决：**
- 检查 `api-key` 配置是否正确
- 确认AI服务的认证方式（Bearer Token、X-API-Key等）
- 如果认证方式不同，需要修改 `AiServiceImpl` 中的请求头设置

### 问题4：响应格式不匹配

**现象：** AI服务返回了响应，但系统无法提取答案

**解决：**
- 检查AI服务的响应格式是否符合规范
- 查看日志中的响应内容
- 如需要，修改 `AiServiceImpl` 中的响应解析逻辑

## 高级配置

### 自定义认证方式

如果您的AI服务使用不同的认证方式，可以修改 `AiServiceImpl.java`：

```java
// 示例：使用X-API-Key认证
headers.set("X-API-Key", aiServiceApiKey);

// 示例：使用Basic认证
String auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
headers.set("Authorization", "Basic " + auth);
```

### 自定义超时配置

可以为不同的AI服务设置不同的超时时间：

```java
@Value("${ai.service.timeout:5000}")
private int timeout;

// 在RestTemplate中配置超时
RestTemplate restTemplate = new RestTemplate();
ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout(timeout);
((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout(timeout);
restTemplate.setRequestFactory(factory);
```

## 注意事项

1. **性能考虑**：AI服务调用是异步的，但会阻塞当前请求。建议AI服务响应时间控制在5秒以内。

2. **错误处理**：系统设计为容错模式，即使AI服务失败，也不会影响用户使用，会自动降级到兜底文案。

3. **多语言支持**：系统会将用户的语言偏好传递给AI服务，AI服务应能够根据 `languageType` 返回对应语言的答案。

4. **上下文传递**：系统会将相关FAQ数量和事项ID传递给AI服务，帮助AI生成更准确的答案。

## 后续优化建议

1. **缓存机制**：可以缓存常见问题的AI回答，减少API调用
2. **异步调用**：将AI调用改为异步，提升用户体验
3. **重试机制**：在AI服务临时不可用时，增加重试逻辑
4. **答案质量评估**：可以添加答案质量评分，过滤低质量回答
