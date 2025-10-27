# Laby Boot

企业级快速开发平台

## 项目说明

本项目基于 Spring Boot 3.x 构建，提供完整的企业级应用开发基础设施。

## 技术栈

### 后端技术

| 框架 | 说明 | 版本 |
|---|---|---|
| Spring Boot | 应用开发框架 | 3.5.5 |
| MySQL | 数据库服务器 | 5.7 / 8.0+ |
| MyBatis Plus | MyBatis 增强工具包 | 3.5.12 |
| Redis | key-value 数据库 | 5.0 / 6.0 /7.0 |
| Spring Security | Spring 安全框架 | 6.5.2 |

### 前端技术

| 框架 | 说明 | 版本 |
|---|---|---|
| Vue | Vue 框架 | 3.3.8 |
| Element Plus | UI组件库 | 2.4.2 |
| TypeScript | JavaScript 的超集 | 5.2.2 |

## 项目结构

| 项目 | 说明 |
|---|---|
| `laby-dependencies` | Maven 依赖版本管理 |
| `laby-framework` | Java 框架拓展 |
| `laby-server` | 服务端主程序 |
| `laby-module-system` | 系统功能模块 |
| `laby-module-infra` | 基础设施模块 |
| `laby-ui` | 前端项目 |

## 主要功能

### 系统功能

- 用户管理：系统用户配置与管理
- 角色管理：角色菜单权限分配、数据范围权限划分
- 菜单管理：系统菜单、操作权限、按钮权限配置
- 部门管理：组织机构管理，树结构展现
- 岗位管理：用户职务配置
- 租户管理：支持 SaaS 多租户功能
- 字典管理：系统固定数据维护
- 操作日志：系统操作日志记录和查询
- 登录日志：系统登录日志记录查询

### 基础设施

- 代码生成：前后端代码生成（Java、Vue、SQL）
- 系统接口：基于 Swagger 自动生成 API 接口文档
- 配置管理：系统动态配置参数
- 定时任务：在线任务调度管理
- 文件服务：支持多种存储方式（本地、MinIO、阿里云OSS等）
- API 日志：API 访问日志、异常日志

## 开发环境要求

- JDK 17+
- Maven 3.6+
- MySQL 5.7+ / 8.0+
- Redis 5.0+
- Node.js 16.18+
- pnpm 8.6+

## 快速开始

### 后端启动

1. 创建数据库并导入初始化脚本
2. 修改配置文件中的数据库连接信息
3. 启动 Redis 服务
4. 运行主程序 `LabyServerApplication`

### 前端启动

```bash
cd laby-ui
pnpm install
pnpm dev
```

## 开源协议

本项目采用 MIT License 开源协议
