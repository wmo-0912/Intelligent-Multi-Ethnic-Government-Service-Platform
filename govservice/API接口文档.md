# 政务服务平台 API接口文档

## 快速开始

### 1. 启动应用
```bash
# 方式1：Maven命令
mvn spring-boot:run

# 方式2：批处理脚本
start.bat

# 方式3：IDE直接运行
运行 GovServiceApplication.java
```

### 2. 访问Swagger文档
启动后访问：`http://localhost:8080/api/swagger-ui.html`

### 3. 初始化数据库
执行 `src/main/resources/sql/init.sql` 初始化数据库和测试数据

### 4. 测试账号
- **管理员**: account=`admin`, password=`test123` (政务人员，userId=1)
- **普通用户**: account=`test`, password=`test123` (居民，userId=37)
- **其他用户**: account=`lisi`, password=`test123` (政务人员，userId=2)

---

## 核心功能模块

### 一、用户管理模块 (`/api/user`)

#### 1.1 用户注册
- **接口**: `POST /user/register`
- **公开接口**（无需token）
- **功能**: 
  - BCrypt加密存储密码
  - 自动实名认证（集成RealNameService）
  - 返回用户基本信息
- **请求参数**:
```json
{
  "name": "张三",
  "ethnicity": "汉族",
  "phoneNumber": "13800000001",
  "account": "zhangsan",
  "password": "123456",
  "role": "居民"
}
```

#### 1.2 用户登录
- **接口**: `POST /user/login` 或 `GET /user/login`
- **公开接口**（无需token）
- **功能**:
  - BCrypt密码校验
  - 签发JWT token（8小时有效期）
  - 返回用户信息、角色权限、多语言配置
- **请求参数**: `account`、`password`（支持query或body）
- **响应示例**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "userId": 1,
    "name": "系统管理员",
    "role": "政务人员",
    "permissions": ["ITEM_REVIEW", "ITEM_MANAGE", "USER_MANAGE"],
    "languageType": "zh-CN",
    "i18nConfig": {
      "direction": "ltr",
      "verticalWriting": false,
      "realNameVerified": true,
      "token": "eyJhbGciOiJIUzI1NiJ9..."
    }
  }
}
```

#### 1.3 刷新Token
- **接口**: `POST /user/refresh`
- **需要token**
- **功能**: 延长token有效期，旧token加入黑名单

#### 1.4 用户登出
- **接口**: `POST /user/logout`
- **需要token**
- **功能**: 将token加入黑名单，立即失效

#### 1.5 获取当前用户信息
- **接口**: `GET /user/profile`
- **需要token**
- **功能**: 从token解析userId，返回完整用户信息

#### 1.6 设置语言偏好
- **接口**: `PUT /user/me/language?languageType=ug`
- **需要token**
- **功能**: 设置当前用户语言偏好（内存存储）

#### 1.7 管理员功能（政务人员专用）
- `GET /user/all` - 查询所有用户
- `GET /user/role/{role}` - 按角色查询用户
- `PUT /user/{userId}/freeze?frozen=true` - 冻结/解冻账号
- `DELETE /user/{userId}` - 删除用户

---

### 二、政务事项模块 (`/api/service-item`)

#### 2.1 查询启用事项
- **接口**: `GET /service-item/enabled`
- **需要token**
- **参数**: 
  - `languageType` - 多语言类型（可选）
  - `keyword` - 关键词搜索（可选）
  - `department` - 部门筛选（可选）

#### 2.2 统一搜索（分页）
- **接口**: `GET /service-item/search`
- **需要token**
- **参数**:
  - `keyword` - 关键词
  - `department` - 部门
  - `onlyEnabled` - 仅启用
  - `page`、`size` - 分页参数
  - `languageType` - 多语言
- **响应示例**:
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "page": 1,
    "size": 10,
    "records": [...]
  }
}
```

#### 2.3 提交事项申请
- **接口**: `POST /service-item/apply`
- **需要token**
- **功能**:
  - 创建办理记录
  - 自动发送站内通知
- **请求参数**:
```json
{
  "userId": 2,
  "itemId": 1,
  "applicationMaterials": "身份证、户口本"
}
```

#### 2.4 查询我的申请
- **接口**: `GET /service-item/applications/me?languageType=ug`
- **需要token**
- **功能**: 从token获取userId，返回申请列表（含多语言状态）

#### 2.5 管理功能（政务人员专用，@GovOnly）
- `POST /service-item` - 创建事项
- `PUT /service-item/{itemId}` - 更新事项
- `PUT /service-item/{itemId}/status?isEnabled=true` - 启用/禁用
- `DELETE /service-item/{itemId}` - 删除事项
- `GET /service-item/applications/pending` - 查询待审核申请（状态为"待审核"或"待处理"）
- `PUT /service-item/applications/{recordId}/handle` - 审批申请
  - **请求示例**: `PUT /service-item/applications/1/handle?status=已通过&handleResult=审核通过，请前往民政局办理&handleUserId=1`
  - **状态流转校验**: 待审核 → 处理中/已通过/已完成/驳回
  - **自动通知**: 审批后自动发送状态变更通知给申请人

---

### 三、文件管理模块 (`/api/file`)

#### 3.1 上传文件
- **接口**: `POST /file/upload`
- **需要token**
- **参数**:
  - `file` - 文件（multipart/form-data）
  - `relatedRecordId` - 关联办理记录ID（可选）
  - `fileCategory` - 文件分类：ID_CARD/PROOF/OTHER
- **限制**:
  - 最大10MB
  - 支持：jpg/png/gif/pdf/doc/docx
- **响应**:
```json
{
  "code": 200,
  "data": {
    "fileId": 1,
    "fileName": "身份证.jpg",
    "storagePath": "uuid.jpg",
    "fileSize": 102400,
    "uploadTime": "2025-10-28T10:00:00"
  }
}
```

#### 3.2 下载文件
- **接口**: `GET /file/download/{fileId}`
- **需要token**
- **功能**: 下载文件，设置Content-Disposition为attachment

#### 3.3 预览文件
- **接口**: `GET /file/preview/{fileId}`
- **需要token**
- **功能**: 浏览器内预览（图片、PDF）

#### 3.4 查询文件
- `GET /file/{fileId}` - 文件信息
- `GET /file/record/{recordId}` - 按办理记录查询
- `GET /file/user/my` - 查询当前用户上传的文件（从token获取userId）

#### 3.5 删除文件
- **接口**: `DELETE /file/{fileId}`
- **需要token**
- **功能**: 逻辑删除

---

### 四、通知消息模块 (`/api/notification`)

#### 4.1 查询我的通知
- **接口**: `GET /notification/my`
- **需要token**
- **功能**: 查询当前用户所有通知（最多100条，按时间倒序）
- **响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "notificationId": 2,
      "userId": 37,
      "title": "申请状态变更",
      "content": "您的申请状态已变更为【已通过】，处理结果：审核通过，请前往民政局办理",
      "notifyType": "APPLICATION_STATUS",
      "relatedRecordId": 1,
      "isRead": false,
      "createTime": "2025-10-30T00:03:37",
      "readTime": null
    },
    {
      "notificationId": 1,
      "userId": 37,
      "title": "申请提交成功",
      "content": "您的【身份证办理】申请已提交，请等待审核。",
      "notifyType": "APPLICATION_STATUS",
      "relatedRecordId": 1,
      "isRead": true,
      "createTime": "2025-10-29T21:14:21",
      "readTime": "2025-10-29T23:51:38"
    }
  ]
}
```

#### 4.2 未读消息数量
- **接口**: `GET /notification/unread-count`
- **需要token**
- **响应**: `{"code": 200, "data": 1}`

#### 4.3 标记已读
- **接口**: `PUT /notification/{notificationId}/read`
- **需要token**
- **示例**: `PUT /notification/2/read`

#### 4.4 全部标记已读
- **接口**: `PUT /notification/read-all`
- **需要token**

#### 通知触发场景（自动发送）
1. **申请提交成功** - 用户提交申请时自动发送
2. **申请状态变更** - 政务人员审批后自动发送（状态：待审核/处理中/已通过/已完成/驳回）

---

### 五、多语言与FAQ模块

#### 5.1 FAQ搜索
- **接口**: `GET /api/faq/search?keyword=办理&languageType=ug`
- **需要token**
- **功能**: 按关键词和语种搜索FAQ

#### 5.2 智能机器人
- **接口**: `POST /api/faq/bot/ask`
- **需要token**
- **请求参数**:
```json
{
  "question": "如何办理身份证？",
  "languageType": "zh-CN"
}
```
- **功能**: 智能匹配FAQ，支持多语言fallback

---

## 技术亮点

### 1. 安全与认证
- **BCrypt密码加密**（替代MD5）
- **JWT token认证**（HS256，8小时有效）
- **Token黑名单**（登出/刷新失效）
- **AuthInterceptor**（全局token校验与注入）
- **@GovOnly注解+AOP**（政务权限统一拦截）

### 2. 数据访问
- **MyBatis注解+XML**（混合映射）
- **动态SQL**（@Select with `<script>`）
- **分页查询**（PageRequest/PageResponse）
- **逻辑删除**（FileInfo.Deleted）

### 3. 业务流程
- **状态流转校验**（待审核 → 处理中 → 已通过/已完成/驳回）
- **自动通知触发**（申请提交/状态变更自动发送站内通知）
- **多语言装饰**（ServiceItem/FAQ/Status翻译，支持18种少数民族语言）
- **文件上传管理**（本地存储，格式/大小校验，uploadTime自动记录）

### 4. 多语言支持
- **18种少数民族语言**（ug/bo/mn/kk/ko/yi/za等）
- **RTL与竖排适配**（登录返回direction/verticalWriting）
- **动态翻译**（I18nUtils加载.properties）
- **语言偏好管理**（PreferenceService内存存储）

### 5. 接口文档
- **Swagger/OpenAPI 3.0**（springdoc-openapi-ui）
- **按模块分组**（用户/事项/文件/通知/多语言/FAQ）
- **JWT认证配置**（Bearer Token全局Header）
- **访问**: `http://localhost:8080/api/swagger-ui.html`

---

## 数据库表结构

### 核心表
1. **User** - 用户表（新增RealNameVerified、Frozen）
2. **ServiceItem** - 政务事项表
3. **HandlingRecord** - 办理记录表
4. **MultilingualInfo** - 多语言信息表
5. **FAQ** - 常见问题表
6. **FileInfo** - 文件信息表（新增）
7. **Notification** - 通知消息表（新增）

### 索引优化
- User(Account, PhoneNumber, Role)
- ServiceItem(DepartmentName, IsEnabled)
- HandlingRecord(UserID, ItemID, Status)
- MultilingualInfo(ItemID, LanguageType)
- FAQ(ItemID, LanguageType, Question)
- FileInfo(UploadUserId, RelatedRecordId)
- Notification(UserId, IsRead)

---

## 联调指南

### 1. Postman测试流程

#### Step 1: 登录获取token
```http
POST http://localhost:8080/api/user/login
Content-Type: application/json

{
  "account": "admin",
  "password": "admin123"
}
```
复制响应中的 `data.i18nConfig.token`

#### Step 2: 配置Authorization Header
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### Step 3: 调用业务接口
```http
GET http://localhost:8080/api/user/profile
Authorization: Bearer <token>

GET http://localhost:8080/api/service-item/search?keyword=身份证&page=1&size=10
Authorization: Bearer <token>

POST http://localhost:8080/api/file/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data
file: [选择文件]
fileCategory: ID_CARD
```

### 2. Swagger在线测试
1. 访问 `http://localhost:8080/api/swagger-ui.html`
2. 点击右上角 `Authorize` 按钮
3. 输入 `Bearer <token>`（注意Bearer后有空格）
4. 在各模块中选择接口直接测试

---

## 下一步建议

### 高优先级
1. **密码重置** - 短信验证码 + 修改密码接口
2. **语言偏好落库** - User表新增LanguageType字段
3. **角色权限模型** - 权限点枚举 + @RequiresPermission注解
4. **事项审核流程** - 多级审核、会签、转办
5. **附件必填校验** - 某些事项强制上传身份证、证明材料

### 中优先级
6. **短信服务对接** - 阿里云/腾讯云短信网关
7. **Redis缓存** - 热点事项、FAQ、token黑名单
8. **Flyway迁移** - 数据库版本管理
9. **单元测试** - Service/Controller层覆盖率>80%
10. **前端脱敏** - 手机号、身份证号部分隐藏

### 低优先级
11. **OSS对接** - 阿里云OSS替代本地存储
12. **Refresh Token机制** - 长期token + 短期access token
13. **操作日志** - AOP记录增删改操作
14. **限流防刷** - 登录、上传接口限流
15. **Docker部署** - Dockerfile + docker-compose

---

## 常见问题

### Q1: 登录报401 Unauthorized
**A**: 
1. 检查账号密码是否正确（admin/test123 或 test/123456）
2. 初始数据库密码已改为BCrypt加密，需重新执行`init.sql`
3. Token可能已过期（有效期8小时），请重新登录

### Q2: 接口报403 Forbidden
**A**: 该接口需要政务人员角色（@GovOnly），使用`admin`或`lisi`账号登录

### Q3: 文件上传失败或找不到路径
**A**: 
1. 检查文件大小（<10MB）和格式（支持jpg/png/gif/pdf/doc/docx）
2. 确认`application.yml`中的`file.upload.dir`配置正确
3. 系统会自动创建`uploads`目录（相对于项目根目录）

### Q4: 数据库连接失败
**A**: 修改`application.yml`中的数据库配置（url/username/password），确保MySQL服务已启动

### Q5: Swagger页面404
**A**: 确认访问路径 `/api/swagger-ui.html`（注意有context-path `/api`）

### Q6: 多语言显示乱码或不生效
**A**: 
1. 确认`src/main/resources/languages/`下有对应语言的`.properties`文件
2. 确认数据库`MultilingualInfo`表中有对应的翻译数据
3. 执行`add_all_minority_languages.sql`添加完整的多语言数据

### Q7: 状态显示为"锟斤拷"
**A**: 字符编码问题，确保：
1. 数据库字符集为`utf8mb4`
2. 状态值使用中文（待审核/处理中/已通过/已完成/驳回）
3. `I18nUtils.translateStatus`正确映射状态键值

---

## 测试验证记录

### ✅ 已完成功能测试（2025-10-30）

#### 1. 用户模块
- [x] 用户注册（BCrypt密码加密）
- [x] 用户登录（普通用户 test/123456）
- [x] 管理员登录（admin/test123）
- [x] Token自动注入和验证
- [x] 语言偏好设置

#### 2. 政务事项模块
- [x] 查询启用事项列表
- [x] 查询事项详情
- [x] 多语言事项名称翻译（蒙古语 mn 测试通过）
- [x] 提交事项申请
- [x] 查询我的申请（/applications/me）
- [x] 多语言状态翻译（待审核/已通过等）

#### 3. 文件管理模块
- [x] 文件上传（图片、PDF、Word文档）
- [x] 文件信息查询
- [x] 我的文件列表（/file/user/my）
- [x] uploadTime自动记录和显示

#### 4. 通知系统模块
- [x] 申请提交自动发送通知
- [x] 审批后自动发送状态变更通知
- [x] 查询我的通知（/notification/my）
- [x] 查询未读通知数量
- [x] 标记通知为已读
- [x] 已读/未读状态管理

#### 5. 政务人员审批流程
- [x] 查询待审核申请（/applications/pending）
- [x] 审批申请（状态变更为"已通过"）
- [x] 状态流转校验（待审核 → 已通过）
- [x] 审批后自动通知申请人
- [x] @GovOnly权限拦截验证

#### 6. 多语言功能
- [x] 多语言事项名称翻译
- [x] 多语言状态翻译
- [x] I18nUtils状态映射（待审核/已通过等）
- [x] 字符编码正确处理

### 完整业务闭环测试流程

```
1. 用户注册 → 2. 用户登录（获取token） → 3. 查询政务事项 
→ 4. 上传申请材料 → 5. 提交申请 → 6. 收到提交成功通知
→ 7. 管理员登录 → 8. 查询待审核申请 → 9. 审批通过
→ 10. 用户收到审批结果通知 → 11. 查询申请状态变更
→ 12. 标记通知为已读 ✅
```

**所有核心功能测试通过！** 🎉

---

## 联系方式
- 技术支持: support@govservice.example.com
- 文档仓库: [GitHub链接]
- Swagger文档: http://localhost:8080/api/swagger-ui.html

