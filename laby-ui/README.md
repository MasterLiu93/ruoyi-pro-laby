# Laby UI

基于 Vue 3 + Element Plus 的管理后台前端项目

## 技术栈

| 框架 | 说明 | 版本 |
|---|---|---|
| Vue | Vue 框架 | 3.3.8 |
| Vite | 开发与构建工具 | 4.5.0 |
| Element Plus | UI组件库 | 2.4.2 |
| TypeScript | JavaScript 的超集 | 5.2.2 |
| pinia | Vue 状态管理 | 2.1.7 |
| vue-router | Vue 路由 | 4.2.5 |
| unocss | 原子 CSS | 0.57.4 |

## 特性

- **最新技术栈**：使用 Vue3、Vite4 等前端前沿技术开发
- **TypeScript**: 应用程序级 JavaScript 的语言
- **主题**: 可配置的主题
- **国际化**：内置完善的国际化方案
- **权限**：内置完善的动态路由权限生成方案
- **组件**：二次封装了多个常用的组件

## 开发环境要求

- Node.js > 16.18.0
- pnpm > 8.6.0 (强制使用pnpm)

## 开发工具

推荐 VS Code 开发，配合插件如下：

| 插件名 | 功能 |
|---|---|
| Vue - Official | Vue 与 TypeScript 支持 |
| unocss | unocss for vscode |
| Iconify IntelliSense | Iconify 预览和搜索 |
| i18n Ally | 国际化智能提示 |
| Stylelint | CSS 格式化 |
| Prettier | 代码格式化 |
| ESLint | 脚本代码检查 |
| DotENV | env 文件高亮 |

## 快速开始

```bash
# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev

# 构建生产环境
pnpm build
```

## 项目结构

```
laby-ui/
├── src/
│   ├── api/          # API 接口
│   ├── assets/       # 静态资源
│   ├── components/   # 公共组件
│   ├── layout/       # 布局组件
│   ├── router/       # 路由配置
│   ├── store/        # 状态管理
│   ├── styles/       # 样式文件
│   ├── utils/        # 工具函数
│   └── views/        # 页面组件
├── public/           # 公共静态文件
└── dist/            # 构建输出目录
```
