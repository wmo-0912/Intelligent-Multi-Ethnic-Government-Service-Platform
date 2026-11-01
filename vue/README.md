# 多民族地区政务服务平台

基于 Vue3 + i18n 的多民族政务服务平台前端项目。

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 下一代前端构建工具
- **Vue Router** - 官方路由管理器
- **Pinia** - Vue 状态管理
- **Vue I18n** - 国际化解决方案
- **Element Plus** - Vue 3 UI 框架

## 支持语言

- 中文 (zh-CN)
- 维吾尔语 (ug)
- 藏语 (bo)
- 蒙古语 (mn)

## 功能模块

### 1. 用户模块
- 用户登录/注册
- 个人信息管理
- 密码修改

### 2. 政务服务模块
- 在线申请
- 服务分类浏览
- 服务详情查看

### 3. 申请管理模块
- 我的申请列表
- 申请进度查询
- 申请详情查看

### 4. 智能咨询模块
- 在线智能问答
- 常见问题快速查询
- 智能助手互动

### 5. 个人中心模块
- 基本信息管理
- 密码修改
- 服务记录

## 项目结构

```
multi-ethnic-government-platform/
├── src/
│   ├── assets/          # 静态资源
│   ├── components/      # 公共组件
│   ├── i18n/           # 国际化配置
│   ├── layouts/        # 布局组件
│   ├── locales/        # 语言包
│   ├── router/         # 路由配置
│   ├── stores/         # 状态管理
│   ├── views/          # 页面组件
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件
├── index.html          # HTML 模板
├── package.json        # 项目配置
├── vite.config.js      # Vite 配置
└── README.md           # 项目说明
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 开发说明

### 添加新语言

1. 在 `src/locales/` 目录下创建新的语言文件
2. 在 `src/i18n/index.js` 中引入并配置新语言
3. 在 `src/layouts/MainLayout.vue` 中添加语言选择选项

### 添加新页面

1. 在 `src/views/` 目录下创建页面组件
2. 在 `src/router/index.js` 中配置路由
3. 如需多语言支持，在语言包中添加对应翻译

## 核心特性

- ✅ 多语言国际化支持
- ✅ 响应式设计，支持移动端
- ✅ 路由权限控制
- ✅ 状态持久化
- ✅ 现代化 UI 设计
- ✅ 组件化开发

## 联系信息

如有问题或建议，请联系开发团队。
