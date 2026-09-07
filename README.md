<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange" alt="java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen" alt="springboot">
  <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.7-blue" alt="mybatis-plus">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1" alt="mysql">
  <img src="https://img.shields.io/badge/Redis-7-red" alt="redis">
  <img src="https://img.shields.io/badge/Vue-3.4-42b883" alt="vue">
  <img src="https://img.shields.io/badge/Element%20Plus-2.7-409eff" alt="element-plus">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="license">
</p>

<h1 align="center">🌱 新芽 Sprout Admin</h1>

<p align="center">一个现代、轻量、开箱即用的 <b>全栈权限管理系统脚手架</b>，内置 RBAC、JWT 认证、操作日志与可视化代码生成器，前后端分离，开箱即用。</p>

<p align="center">
  <a href="#-功能特性">功能特性</a> ·
  <a href="#-技术栈">技术栈</a> ·
  <a href="#-快速开始">快速开始</a> ·
  <a href="#-项目结构">项目结构</a> ·
  <a href="#-接口文档">接口文档</a> ·
  <a href="#-代码生成器">代码生成器</a>
</p>

---

## ✨ 功能特性

| 模块 | 说明 |
|------|------|
| 🔐 **认证授权** | 基于 JWT 的无状态认证，Spring Security 6 权限体系，支持 `@PreAuthorize` 注解级鉴权 |
| 👥 **用户管理** | 用户 CRUD、分页查询、重置密码、分配角色 |
| 🎭 **角色管理** | 角色 CRUD、菜单权限分配 |
| 📋 **菜单管理** | 目录/菜单/按钮三级权限，树形结构 |
| 🏢 **部门管理** | 树形组织架构，祖级列表（ancestors）维护 |
| 📝 **日志管理** | 登录日志、操作日志（`@Log` 注解 + AOP 自动记录） |
| ⚙️ **代码生成器** | 连接数据库 → 勾选表 → 一键生成 Entity/Mapper/Service/Controller 代码 |

## 🛠 技术栈

**后端**

- **核心框架**：Spring Boot 3.2、Java 17
- **安全**：Spring Security 6 + JWT（jjwt 0.12）
- **ORM**：MyBatis-Plus 3.5（逻辑删除、分页、字段自动填充）
- **存储**：MySQL 8、Redis 7（令牌缓存）
- **文档**：Knife4j + OpenAPI3
- **模板引擎**：Apache Velocity（代码生成）
- **工具**：Lombok、Hutool

**前端**

- **框架**：Vue 3.4（组合式 API + `<script setup>`）
- **构建**：Vite 5
- **UI 组件库**：Element Plus 2.7
- **状态管理**：Pinia
- **路由**：Vue Router 4（含登录权限守卫）
- **HTTP**：Axios（统一封装、令牌注入、错误处理）

## 🚀 快速开始

### 环境要求

| 软件 | 版本 |
|------|------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 5.7+ / 8.0 |
| Redis | 6+ |

### 1. 启动中间件

```bash
docker-compose up -d
```

> 这会一键启动 MySQL（自动执行 `db/init.sql` 建库建表）和 Redis。

或手动：执行 `db/init.sql`，并在 `application-dev.yml` 中修改数据库/Redis 连接。

### 2. 启动后端

```bash
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

> 前端开发服务器默认运行在 http://localhost:5173，已配置代理将 `/api` 请求转发到后端 `8080` 端口。

### 4. 访问

| 入口 | 地址 |
|------|------|
| 前端界面 | http://localhost:5173 |
| 接口文档 | http://localhost:8080/doc.html |
| 默认账号 | `admin` / `admin123` |

### 登录示例

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

返回的 `token` 放入后续请求头：

```bash
curl http://localhost:8080/system/user/list \
  -H "Authorization: Bearer <你的token>"
```

## 📁 项目结构

```
sprout-admin
├── db/
│   └── init.sql                 # 建表 + 初始数据
├── docs/
│   ├── 设计文档.md               # 架构与关键设计
│   ├── 数据库设计.md             # 表结构与 E-R 关系
│   └── 开发计划.md               # 分阶段路线图
├── src/main/java/com/sprout/    # 后端（Spring Boot 3）
│   ├── common/                  # 通用：Result、异常、工具类、常量
│   ├── config/                  # 配置：Security、MyBatis-Plus、Redis、跨域、文档
│   ├── security/                # 安全：JWT 过滤器、登录用户、异常处理
│   ├── system/                  # 系统：用户/角色/菜单/部门 + 认证
│   ├── log/                     # 日志：登录日志、操作日志（注解+AOP）
│   └── generator/               # 代码生成器
└── frontend/                    # 前端（Vue 3 + Element Plus）
    └── src/
        ├── api/                 # 接口封装
        ├── layout/              # 布局（侧边栏 + 顶栏）
        ├── router/              # 路由 + 权限守卫
        ├── store/               # Pinia 状态管理
        ├── utils/               # axios 封装、token 工具
        └── views/               # 页面（登录、首页、系统管理、日志、代码生成）
```

## ⚙️ 代码生成器

1. 打开接口文档 → `代码生成` 模块
2. 调用「查询可导入的数据库表」选择要生成的表
3. 调用「导入表」自动读取表结构与字段
4. 修改生成配置（类名、包名、模块名、作者等）
5. 调用「预览代码」查看效果，或「生成代码」下载 zip

生成产物（放入对应包即可直接使用）：

```
com.sprout.business/
├── entity/      实体类（继承 BaseEntity，含逻辑删除）
├── mapper/      MyBatis-Plus Mapper
├── service/     Service 接口
├── service/impl Service 实现
└── controller/  REST 接口（含权限注解、Swagger 注解）
```

## 🔑 RBAC 权限模型

```
用户(User) ──多对多── 角色(Role) ──多对多── 菜单(Menu)
                                    │
                                    ├── 目录(M)  → 前端路由
                                    ├── 菜单(C)  → 前端页面 + 权限标识
                                    └── 按钮(F)  → 权限标识（如 system:user:add）
```

登录时后端加载用户的**角色**与**权限标识**存入 `LoginUser`，接口通过 `@PreAuthorize("hasAuthority('system:user:list')")` 进行方法级鉴权。

## 📄 许可证

[MIT](LICENSE)

## 🤝 贡献与致谢

欢迎提交 Issue / PR。如果这个项目对你有帮助，请给一个 ⭐ Star！
