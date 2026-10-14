# 多民族政务服务平台（后端）

> 一个面向多民族、多语言居民的**政务服务平台后端系统**。通过多语言能力（支持 19 种语言/方言）与智能政务问答，为不同民族的居民提供政务事项查询、在线申请、材料上传与通知跟踪等一体化政务服务，旨在降低语言障碍、提升政务办理效率与民族团结力。

> 说明：本仓库为**后端服务**，基于 Spring Boot 构建；前端页面与第三方大模型 API 需按下方说明另行接入。

---

## ✨ 功能特性

### 多语言支持
- 内置 **19 种语言/方言**资源：中文、蒙古语、藏语、维吾尔语、哈萨克语、朝鲜语、彝语、壮语、布依语、苗语、侗语、瑶语、白语、哈尼语、傣语、傈僳语、拉祜语、佤语、纳西语
- 政务事项名称、办理要求、FAQ 均支持**按语种展示与维护**
- 语种资源位于 `src/main/resources/languages/*.properties`，可灵活扩展

### 用户与认证
- 注册、登录（密码使用 **BCrypt** 加密）
- **JWT Token** 会话鉴权，支持登出（Token 黑名单）
- 双角色：**居民** 与 **政务人员**（`GovOnly` 注解 + AOP 切面控制政务人员权限）
- 实名认证标识、账号冻结机制

### 政务事项与办理
- 政务事项的发布与管理（名称、所属部门、办理要求、启用状态）
- 居民在线提交**办理申请**，政务人员处理（待处理 / 处理中 / 已完成 / 驳回）
- 申请材料**文件上传**（本地存储，可配置目录、大小限制）

### 智能问答（FAQ）
- 政务 FAQ 的多语言**关键词检索问答**（`/api/faq/bot/ask`）
- FAQ 访问量统计、按事项/语种检索
- ⚠️ 当前为 **FAQ 检索占位实现**，可进一步接入智谱 AI 等大模型 API 升级为真正智能问答（见"注意事项"）

### 通知与运维
- 站内通知消息（办理状态变更 / 系统 / 公告），已读管理
- Spring Boot Actuator 健康监控（独立端口 8081）
- Swagger/OpenAPI 接口文档，按模块分组

---

## 🛠 技术栈

| 分类 | 技术 | 说明 |
| --- | --- | --- |
| 框架 | Spring Boot 2.7.18 | Web 服务 |
| 语言 | Java 8 | 运行环境 |
| 持久层 | MyBatis（XML 映射） | 数据访问 |
| 数据库 | MySQL 8.x | 数据存储 |
| 认证 | JWT (jjwt 0.9.1) + BCrypt | Token 鉴权 + 密码加密 |
| 权限 | Spring AOP（`@GovOnly` 切面） | 政务人员权限控制 |
| 接口文档 | springdoc-openapi (Swagger) | 在线调试 |
| 监控 | Spring Boot Actuator | 健康检查 |
| 参数校验 | spring-boot-starter-validation | Bean Validation |
| 工程化 | Lombok | 简化代码 |

---

## 📁 项目结构

```
govservice
├── src/main/java/com/example/govservice
│   ├── controller/       # 接口层：用户/政务事项/多语言/FAQ/文件/通知/配置/健康检查
│   ├── service/          # 业务接口 + impl 实现（含多语言、文件存储、短信、实名、Token黑名单等）
│   ├── mapper/           # MyBatis Mapper
│   ├── entity/           # 实体类（User/ServiceItem/HandlingRecord/MultilingualInfo/FAQ/FileInfo/Notification）
│   ├── dto/              # 请求/响应 DTO
│   ├── config/           # 配置：MyBatis/OpenApi/Web/多语言/鉴权拦截器/AOP
│   ├── exception/        # 统一异常与错误码
│   ├── util/             # 工具类（JWT、加密、I18n、日期、字符串）
│   └── GovServiceApplication.java
├── src/main/resources
│   ├── application.yml / application-dev.yml   # 环境配置
│   ├── languages/*.properties                 # 19 种多语言资源
│   ├── mybatis/*.xml                          # MyBatis 映射文件
│   ├── sql/init.sql                           # 建库建表 + 初始数据（含多语言与FAQ）
│   └── static/index.html                      # 简易页面
├── uploads/             # 文件上传目录
├── Postman测试集合.json  # 接口测试集合
└── pom.xml
```

### 数据表
| 表 | 说明 |
| --- | --- |
| `User` | 用户（账号、密码、民族、角色、实名认证、冻结状态） |
| `ServiceItem` | 政务事项（名称、所属部门、办理要求） |
| `HandlingRecord` | 办理记录（状态流转、处理结果、申请材料） |
| `MultilingualInfo` | 多语言信息（事项的语种翻译） |
| `FAQ` | 常见问题（多语种问题/答案、访问量） |
| `FileInfo` | 上传文件信息（存储路径、关联记录、分类） |
| `Notification` | 通知消息（类型、已读状态） |

---

## 🚀 快速开始

### 环境要求
- JDK **1.8**+
- Maven（或使用项目自带 `mvnw`）
- MySQL **8.x**

### 1. 初始化数据库
导入 `src/main/resources/sql/init.sql`（会自动创建 `govservice` 库、建表并插入初始数据，含示例用户、政务事项、多语言翻译与 FAQ）：

```bash
mysql -u root -p < src/main/resources/sql/init.sql
```

### 2. 修改数据库配置
编辑 `src/main/resources/application.yml`，将数据源改为你自己的账号密码：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/govservice?allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    username: root
    password: 你的数据库密码
```

### 3. 启动服务（端口 8080，上下文前缀 `/api`）
```bash
# 方式一：Maven 包装器
./mvnw spring-boot:run
# 方式二：打包运行
mvn clean package -DskipTests
java -jar target/govservice-0.0.1-SNAPSHOT.jar
```

### 4. 访问接口文档
- Swagger UI：**http://localhost:8080/api/swagger-ui.html**
- OpenAPI 文档：**http://localhost:8080/api/v3/api-docs**
- Actuator 健康检查：**http://localhost:8081/actuator/health**

---

## 🔑 初始账号（来自 init.sql）
| 账号 | 密码 | 角色 | 说明 |
| --- | --- | --- | --- |
| `admin` | `admin123` | 政务人员 | 系统管理员 |
| `zhangsan` | `123456` | 居民 | 已实名 |
| `lisi` | `123456` | 居民 | 维吾尔族 |
| `wangwu` | `123456` | 居民 | 藏族 |

> ⚠️ 上线前请务必修改初始密码。

---

## 🌐 常用接口（部分）
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/user/register` | 注册 |
| POST | `/api/user/login` | 登录（返回 JWT） |
| POST | `/api/user/logout` | 登出（加入 Token 黑名单） |
| GET | `/api/service-item/**` | 政务事项查询 |
| POST | `/api/service-item/**` | 办理申请（政务人员可管理） |
| GET/POST/PUT/DELETE | `/api/multilingual/**` | 多语言信息管理 |
| GET/POST | `/api/faq/**` | FAQ 查询、检索与机器人问答 |
| POST | `/api/faq/bot/ask` | 多语言智能问答（当前为 FAQ 检索） |
| GET/POST | `/api/file/**` | 文件上传 / 下载 |
| GET/POST | `/api/notification/**` | 通知消息 |

> 完整接口按模块分组展示在 Swagger 中；`Postman测试集合.json` 提供现成的接口测试用例。

---

## ⚠️ 注意事项
- **大模型问答**：当前 `/api/faq/bot/ask` 为 **FAQ 关键词检索占位实现**（命中即返回 FAQ 答案，未命中返回多语言兜底文案），并未调用任何第三方大模型 API。如需接入智谱 AI 等大模型，可在此接口中补充 HTTP 调用与多语言提示词逻辑。
- **前端**：本仓库仅含后端及 `static/index.html` 简易页面，未包含 Vue3 前端工程；如需完整前后端项目，请另附前端代码或说明。
- **敏感配置**：`application.yml` / `application-dev.yml` 中写有本地数据库密码，推送公开仓库前请改用环境变量或忽略敏感文件。
- 当前为演示/学习用途，正式部署需补充更完善的权限校验、限流与安全加固。

---

## 📄 许可证
本项目为个人学习/演示项目，未指定开源许可证。
