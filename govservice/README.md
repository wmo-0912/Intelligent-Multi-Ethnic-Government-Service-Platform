# 政务服务平台后端系统
当前有效的注册码：
GOV2024
GOV2025
ADMIN2024
## 项目简介

这是一个基于Spring Boot开发的政务服务平台后端系统，支持多语言（中文、维吾尔语、藏语），提供用户管理、政务事项办理、智能咨询等功能。

## 技术栈

- **框架**: Spring Boot 2.6.13
- **数据库**: MySQL 8.0
- **ORM**: MyBatis
- **构建工具**: Maven
- **Java版本**: JDK 1.8
- **其他**: Lombok, Validation

## 功能模块

### 1. 用户管理模块
- **居民功能**:
  - 用户注册（姓名、民族、联系方式、账号密码）
  - 用户登录（账号密码校验）
  - 个人信息查询/修改
- **政务人员功能**:
  - 登录（账号密码+权限校验）
  - 用户权限管理
  - 查询居民/政务人员列表

### 2. 政务事项办理模块
- **事项查询**:
  - 按部门/类型查询政务事项列表
  - 查看单个事项详情
- **事项申请**:
  - 居民提交事项申请
  - 申请记录查询
- **事项处理**:
  - 待处理事项列表
  - 审核/驳回申请
  - 办理统计

### 3. 多语言支持模块
- 语言切换（中文、维吾尔语、藏语）
- 多语言数据管理
- 事项多语言版本维护

### 4. 智能咨询模块
- 居民咨询提交
- FAQ管理
- 咨询回复查询
- FAQ访问量统计

## 项目结构

```
src/main/java/com/example/govservice/
├── GovServiceApplication.java          # 项目启动类
├── config/                             # 配置类
│   ├── MyBatisConfig.java             # MyBatis配置
│   ├── LanguageConfig.java            # 多语言配置
│   └── WebConfig.java                 # Web配置
├── controller/                         # 控制器层
│   ├── UserController.java            # 用户管理接口
│   ├── ServiceItemController.java     # 政务事项接口
│   └── MultilingualController.java   # 多语言接口
├── service/                           # 业务逻辑层
│   ├── UserService.java              # 用户服务接口
│   ├── ServiceItemService.java       # 政务事项服务接口
│   ├── MultilingualService.java     # 多语言服务接口
│   └── impl/                         # 服务实现类
├── mapper/                           # 数据访问层
│   ├── UserMapper.java              # 用户数据操作
│   ├── ServiceItemMapper.java       # 政务事项数据操作
│   └── MultilingualMapper.java      # 多语言数据操作
├── entity/                           # 实体类
│   ├── User.java                    # 用户实体
│   ├── ServiceItem.java             # 政务事项实体
│   └── MultilingualInfo.java       # 多语言实体
├── dto/                             # 数据传输对象
│   ├── request/                     # 请求参数
│   └── response/                    # 响应参数
├── exception/                       # 异常处理
│   ├── BusinessException.java       # 业务异常
│   ├── GlobalExceptionHandler.java # 全局异常处理器
│   └── ErrorCode.java              # 错误码枚举
└── util/                           # 工具类
    ├── StringUtils.java            # 字符串工具
    ├── DateUtils.java             # 日期工具
    └── EncryptUtils.java          # 加密工具
```

## 数据库设计

### 主要表结构

1. **User表** - 用户信息
2. **ServiceItem表** - 政务事项
3. **HandlingRecord表** - 办理记录
4. **MultilingualInfo表** - 多语言信息
5. **FAQ表** - 常见问题

## 快速开始

### 1. 环境要求
- JDK 1.8+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置
1. 创建数据库：
```sql
CREATE DATABASE govservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p govservice < src/main/resources/sql/init.sql
```

### 3. 配置文件修改
修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/govservice?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    username: your_username
    password: your_password
```

### 4. 运行项目

**方式一：使用Maven命令**
```bash
mvn spring-boot:run
```

**方式二：使用启动脚本（Windows）**
```bash
start.bat
```

**方式三：使用IDE**
- 在IDE中运行 `com.example.govservice.GovServiceApplication` 主类

项目启动后访问：
- 主服务：http://localhost:8080/api
- 健康检查：http://localhost:8080/api/health/check
- 系统信息：http://localhost:8080/api/health/info

## API接口文档

### 用户管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |
| GET | /api/user/{userId} | 查询用户信息 |
| PUT | /api/user/{userId} | 更新用户信息 |
| GET | /api/user/all | 查询所有用户 |
| DELETE | /api/user/{userId} | 删除用户 |

### 政务事项接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/service-item/{itemId} | 查询事项详情 |
| GET | /api/service-item/enabled | 查询启用的事项 |
| GET | /api/service-item/department/{name} | 按部门查询事项 |
| POST | /api/service-item/apply | 提交事项申请 |
| GET | /api/service-item/applications/user/{userId} | 查询用户申请记录 |
| PUT | /api/service-item/applications/{recordId}/handle | 处理申请 |

### 多语言接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/multilingual/item/{itemId}/language/{type} | 获取多语言信息 |
| POST | /api/multilingual | 创建多语言信息 |
| PUT | /api/multilingual/{languageId} | 更新多语言信息 |

### FAQ接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/faq/item/{itemId} | 查询事项FAQ |
| GET | /api/faq/search | 搜索FAQ |
| POST | /api/faq | 创建FAQ |
| PUT | /api/faq/{questionId} | 更新FAQ |

## 统一响应格式

所有API接口都返回统一的响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 1001 | 参数错误 |
| 2001 | 用户不存在 |
| 2002 | 用户已存在 |
| 2005 | 登录失败 |
| 3001 | 事项不存在 |
| 4001 | 申请记录不存在 |
| 5001 | 不支持的语言类型 |
| 6001 | FAQ不存在 |
| 500 | 系统内部错误 |

## 开发说明

### 1. 代码规范
- 使用Lombok减少样板代码
- 统一异常处理
- 参数校验使用Bean Validation
- 日志记录使用SLF4J

### 2. 数据库操作
- 使用MyBatis进行数据库操作
- 支持驼峰命名转换
- 使用XML映射文件编写SQL

### 3. 多语言支持
- 支持中文、维吾尔语、藏语
- 使用properties文件存储多语言资源
- 支持动态语言切换

## 部署说明

### 1. 打包项目
```bash
mvn clean package
```

### 2. 运行JAR包
```bash
java -jar target/govservice-0.0.1-SNAPSHOT.jar
```

### 3. Docker部署（可选）
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/govservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

MIT License

## 联系方式

如有问题，请联系开发团队。
