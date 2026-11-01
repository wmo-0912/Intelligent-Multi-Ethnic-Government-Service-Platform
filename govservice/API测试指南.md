# 政务服务平台 API 测试指南

## 📖 导入Postman集合

1. 打开Postman
2. 点击 Import
3. 选择 `Postman测试集合.json`
4. 创建环境变量 `token`（登录后自动保存）

---

## 🚀 快速测试流程

### 第一步：登录获取Token

**Postman:**
```
POST http://localhost:8080/api/user/login?account=test&password=test123
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/api/user/login?account=test&password=test123"
```

**响应示例:**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "userId": 5,
    "name": "测试",
    "role": "居民",
    "permissions": ["ITEM_QUERY", "ITEM_APPLY"],
    "languageType": "zh-CN",
    "i18nConfig": {
      "direction": "ltr",
      "verticalWriting": false,
      "realNameVerified": false,
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjUsInJvbGUiOiLlsYXmsJEiLCJsYW5ndWFnZVR5cGUiOiJ6aC1DTiIsInN1YiI6IjUiLCJpYXQiOjE3MzAyMDQwOTUsImV4cCI6MTczMDIzMjg5NX0.xxxxx"
    }
  }
}
```

**复制token并替换下面的 `<TOKEN>`**

---

### 第二步：查询政务事项（首页展示）

**搜索事项（分页）:**
```bash
curl -X GET "http://localhost:8080/api/service-item/search?keyword=身份证&page=1&size=10&onlyEnabled=true" \
  -H "Authorization: Bearer <TOKEN>"
```

**按部门查询:**
```bash
# 公安局事务
curl -X GET "http://localhost:8080/api/service-item/search?department=公安局&onlyEnabled=true" \
  -H "Authorization: Bearer <TOKEN>"

# 工商局事务
curl -X GET "http://localhost:8080/api/service-item/search?department=工商局&onlyEnabled=true" \
  -H "Authorization: Bearer <TOKEN>"
```

**多语言查询（维吾尔语）:**
```bash
curl -X GET "http://localhost:8080/api/service-item/search?keyword=身份证&languageType=ug" \
  -H "Authorization: Bearer <TOKEN>"
```

---

### 第三步：在线申请（提交申请）

```bash
curl -X POST "http://localhost:8080/api/service-item/apply" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 5,
    "itemId": 1,
    "applicationMaterials": "已上传：身份证正反面、户口本首页"
  }'
```

**响应:**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "recordId": 1,
    "userId": 5,
    "itemId": 1,
    "status": "待处理",
    "submitTime": "2025-10-29T20:30:00"
  }
}
```

> **自动通知**: 提交后会自动发送站内通知

---

### 第四步：进度查询（查看我的申请）

```bash
curl -X GET "http://localhost:8080/api/service-item/applications/me?languageType=zh-CN" \
  -H "Authorization: Bearer <TOKEN>"
```

**响应示例:**
```json
{
  "code": 200,
  "data": [
    {
      "recordId": 1,
      "userId": 5,
      "itemId": 1,
      "status": "待处理",
      "submitTime": "2025-10-29T20:30:00",
      "handleResult": null,
      "handleTime": null
    }
  ]
}
```

---

### 第五步：智能咨询（FAQ机器人）

```bash
curl -X POST "http://localhost:8080/api/faq/bot/ask" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "如何办理身份证？",
    "languageType": "zh-CN"
  }'
```

**FAQ搜索:**
```bash
curl -X GET "http://localhost:8080/api/faq/search?keyword=身份证&languageType=zh-CN" \
  -H "Authorization: Bearer <TOKEN>"
```

---

### 第六步：文件上传（申请材料）

```bash
curl -X POST "http://localhost:8080/api/file/upload" \
  -H "Authorization: Bearer <TOKEN>" \
  -F "file=@/path/to/id_card.jpg" \
  -F "fileCategory=ID_CARD" \
  -F "relatedRecordId=1"
```

**查看我上传的文件:**
```bash
curl -X GET "http://localhost:8080/api/file/my-files" \
  -H "Authorization: Bearer <TOKEN>"
```

---

### 第七步：通知消息

**查看我的通知:**
```bash
curl -X GET "http://localhost:8080/api/notification/my-notifications?page=1&size=10" \
  -H "Authorization: Bearer <TOKEN>"
```

**未读消息数:**
```bash
curl -X GET "http://localhost:8080/api/notification/unread-count" \
  -H "Authorization: Bearer <TOKEN>"
```

**标记已读:**
```bash
curl -X PUT "http://localhost:8080/api/notification/1/read" \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 🔐 政务人员专用接口

**使用政务账号登录:**
```bash
# 更新admin密码后使用
curl -X POST "http://localhost:8080/api/user/login?account=admin&password=test123"
```

**查询待处理申请:**
```bash
curl -X GET "http://localhost:8080/api/service-item/applications/pending" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

**处理申请（审核）:**
```bash
curl -X PUT "http://localhost:8080/api/service-item/applications/1/handle?status=已完成&handleResult=审核通过&handleUserId=1" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

**创建新事项:**
```bash
curl -X POST "http://localhost:8080/api/service-item" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "itemName": "护照办理",
    "departmentName": "公安局",
    "handleRequirement": "1. 身份证原件\n2. 户口本\n3. 照片2张",
    "isEnabled": true
  }'
```

**启用/禁用事项:**
```bash
curl -X PUT "http://localhost:8080/api/service-item/1/status?isEnabled=false" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

---

## 🌍 多语言测试

**维吾尔语:**
```bash
# 设置语言偏好
curl -X PUT "http://localhost:8080/api/user/me/language?languageType=ug" \
  -H "Authorization: Bearer <TOKEN>"

# 查询事项（维语）
curl -X GET "http://localhost:8080/api/service-item/search?languageType=ug" \
  -H "Authorization: Bearer <TOKEN>"
```

**支持的语言代码:**
- `zh-CN` - 中文
- `ug` - 维吾尔语（RTL）
- `bo` - 藏语（竖排）
- `mn` - 蒙古语
- `kk` - 哈萨克语
- `ko` - 朝鲜语
- 等18种少数民族语言

---

## 📱 前端页面对应的API

### 首页功能模块

| 前端模块 | API接口 | 说明 |
|---------|---------|------|
| **在线申请** | POST /service-item/apply | 提交事项申请 |
| **进度查询** | GET /service-item/applications/me | 查询我的申请 |
| **智能咨询** | POST /faq/bot/ask | AI问答 |
| **民生事务** | GET /service-item/search?department=民政局 | 按部门筛选 |
| **营业执照** | GET /service-item/search?keyword=营业执照 | 关键词搜索 |
| **社会保障** | GET /service-item/search?department=人社局 | 按部门筛选 |
| **教育服务** | GET /service-item/search?department=教育局 | 按部门筛选 |
| **住房服务** | GET /service-item/search?department=住建局 | 按部门筛选 |

---

## ⚙️ 环境变量配置（Postman）

创建 Environment：`政务服务-本地`

| 变量名 | 初始值 | 当前值 |
|--------|--------|--------|
| baseUrl | http://localhost:8080/api | |
| token | | (登录后自动填充) |
| userId | | (登录后手动填充) |

---

## 🧪 测试场景示例

### 场景1：居民办理身份证流程

```bash
# 1. 注册/登录
curl -X POST "http://localhost:8080/api/user/login?account=test&password=test123"
# 复制token

# 2. 搜索身份证办理事项
curl -X GET "http://localhost:8080/api/service-item/search?keyword=身份证" \
  -H "Authorization: Bearer <TOKEN>"
# 获取itemId=1

# 3. 上传身份证照片
curl -X POST "http://localhost:8080/api/file/upload" \
  -H "Authorization: Bearer <TOKEN>" \
  -F "file=@id_front.jpg" \
  -F "fileCategory=ID_CARD"

# 4. 提交申请
curl -X POST "http://localhost:8080/api/service-item/apply" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"userId": 5, "itemId": 1, "applicationMaterials": "身份证照片已上传"}'

# 5. 查看通知（自动收到提交成功通知）
curl -X GET "http://localhost:8080/api/notification/my-notifications" \
  -H "Authorization: Bearer <TOKEN>"

# 6. 查询进度
curl -X GET "http://localhost:8080/api/service-item/applications/me" \
  -H "Authorization: Bearer <TOKEN>"
```

### 场景2：政务人员审核流程

```bash
# 1. 政务登录
curl -X POST "http://localhost:8080/api/user/login?account=admin&password=test123"

# 2. 查看待处理申请
curl -X GET "http://localhost:8080/api/service-item/applications/pending" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"

# 3. 审核通过
curl -X PUT "http://localhost:8080/api/service-item/applications/1/handle?status=已完成&handleResult=审核通过，请到窗口领取&handleUserId=1" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"

# 4. 居民收到状态变更通知（自动发送）
```

---

## 🔧 常见问题

### Q1: 401 Unauthorized
**原因**: Token过期或无效  
**解决**: 重新登录获取新token

### Q2: 403 Forbidden
**原因**: 非政务人员访问政务接口  
**解决**: 使用admin账号登录

### Q3: 文件上传失败
**检查**:
- 文件大小 < 10MB
- 文件格式：jpg/png/pdf/doc/docx
- Header包含正确的token

### Q4: 查不到多语言内容
**原因**: 数据库中MultilingualInfo表数据不足  
**解决**: 执行init.sql中的多语言数据插入

---

## 📊 完整测试清单

- [ ] 用户注册
- [ ] 用户登录
- [ ] 查询事项列表（分页）
- [ ] 关键词搜索事项
- [ ] 按部门筛选事项
- [ ] 多语言查询
- [ ] 提交申请
- [ ] 查询申请进度
- [ ] 上传文件
- [ ] 下载文件
- [ ] 查看通知
- [ ] 标记已读
- [ ] FAQ搜索
- [ ] 智能问答
- [ ] [政务]审核申请
- [ ] [政务]创建事项
- [ ] Token刷新
- [ ] 用户登出

---

**完整Postman集合已保存到 `Postman测试集合.json`，可直接导入使用！**

