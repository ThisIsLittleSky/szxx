# 新时代中国传统优秀文化成就思政素材智能检索学习系统 V1.0

> **汇五千年文脉，铸新时代思政。** —— 让每一份传统文化瑰宝，都能在思政课堂上熠熠生辉。

基于 Spring Boot + Vue 3 的思政教学素材智能管理与学习平台，支持素材上传、智能检索、在线学习、课件导出等功能。

## 技术栈

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.7-1E90FF?style=for-the-badge&logo=mybatis&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-0.12.6-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D?style=for-the-badge&logo=vuedotjs&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.x-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Element Plus](https://img.shields.io/badge/Element_Plus-2.14-409EFF?style=for-the-badge&logo=element&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-8.x-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Pinia](https://img.shields.io/badge/Pinia-3.x-FFD700?style=for-the-badge&logo=vuedotjs&logoColor=black)

![Apache POI](https://img.shields.io/badge/Apache_POI-5.2.5-2E8B57?style=for-the-badge&logo=apache&logoColor=white)
![PDFBox](https://img.shields.io/badge/PDFBox-3.0.2-B22222?style=for-the-badge&logo=apache&logoColor=white)
![jieba](https://img.shields.io/badge/jieba--analysis-1.0.2-FF6B6B?style=for-the-badge&logo=python&logoColor=white)

## 项目结构

```
szxx/
├── init.sql                    # 数据库初始化脚本（11 张表 + 种子数据）
├── szxx-server/                # Spring Boot 后端（92 个 Java 源文件）
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/szxx/
│       │   ├── common/         # 统一响应、全局异常、配置
│       │   ├── security/       # JWT 认证、权限控制
│       │   ├── entity/         # 11 个实体类
│       │   ├── mapper/         # 11 个 MyBatis-Plus Mapper
│       │   ├── dto/            # 请求/响应 DTO
│       │   ├── service/        # 10 个业务服务接口 + 9 个实现
│       │   ├── controller/     # 14 个 REST 控制器
│       │   └── util/           # 分词、Word/PDF 解析工具
│       └── test/               # 5 个单元测试类
├── szxx-client-web/            # 学生/教师端 Vue 3 前端
│   └── src/
│       ├── api/                # Axios 封装 + API 接口
│       ├── router/             # 路由配置 + 鉴权守卫
│       ├── stores/             # Pinia 用户状态
│       └── views/              # 页面组件
└── szxx-admin-web/             # 管理后台 Vue 3 前端
    └── src/（同上结构）
```

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.8+
- Node.js 18+

### 1. 初始化数据库

```bash
mysql -uroot -p < init.sql
```

默认数据库名 `szxx_system`，端口 3307。可在 `application-dev.yml` 中修改。

### 2. 启动后端

**方式一：Maven 插件运行（开发推荐）**

```bash
cd szxx-server
mvn spring-boot:run
```

**方式二：打包运行**

```bash
cd szxx-server
mvn clean package -DskipTests
java -jar target/szxx-server-1.0.0.jar --spring.profiles.active=dev
```

服务运行在 `http://localhost:8089`。

### 3. 启动前端

```bash
# 学生/教师端（端口 5173）
cd szxx-client-web
npm install
npm run dev

# 管理后台（端口 5174）
cd szxx-admin-web
npm install
npm run dev
```

### 4. 创建上传目录

```bash
mkdir D:/szxx-storage
```

## API 概览（41 个端点）

### 公开接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/auth/register` | 用户注册 |
| POST | `/api/v1/auth/login` | 用户登录 |
| POST | `/api/v1/auth/logout` | 用户登出 |
| GET | `/api/v1/categories` | 获取全部分类（朝代/品类/学段） |
| GET | `/api/v1/tags` | 获取全部标签 |

### 用户接口（需认证）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/user/profile` | 获取个人信息 |
| PUT | `/api/v1/user/profile` | 修改个人信息 |
| PUT | `/api/v1/user/password` | 修改密码 |

### 素材接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/v1/materials` | 分页浏览（支持朝代/品类/学段/关键词/排序筛选） | 认证 |
| GET | `/api/v1/materials/{id}` | 查看素材详情 | 认证 |
| POST | `/api/v1/materials` | 创建素材（含标签绑定、封面上传、附件上传） | 教师/管理员 |
| PUT | `/api/v1/materials/{id}` | 编辑素材 | 教师/管理员 |
| DELETE | `/api/v1/materials/{id}` | 删除素材 | 教师/管理员 |
| POST | `/api/v1/materials/batch-upload` | 批量上传 Word/PDF（自动解析标题+正文） | 教师/管理员 |

### 智能检索
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/search?keyword=` | 全文检索（结巴分词 + 高亮） |
| GET | `/api/v1/search/suggest` | 搜索建议 |
| GET | `/api/v1/search/hot` | 热门搜索词 |

### 收藏
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/favorites/toggle/{id}` | 收藏/取消收藏 |
| GET | `/api/v1/favorites/check/{id}` | 检查是否已收藏 |
| GET | `/api/v1/favorites` | 我的收藏列表 |
| DELETE | `/api/v1/favorites/batch` | 批量取消收藏 |

### 学习记录
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/learning/report` | 上报学习进度 |
| GET | `/api/v1/learning/records` | 学习记录列表 |
| GET | `/api/v1/learning/stats` | 学习统计 |

### 文件
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/files/upload/image` | 上传图片 |
| POST | `/api/v1/files/upload/avatar` | 上传头像 |
| GET | `/api/v1/files/download/{id}` | 下载附件 |

### 课件导出
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/v1/export/word` | 导出素材为 Word 课件 | 教师/管理员 |

### 管理后台（需管理员权限）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/admin/dashboard` | 数据仪表盘 |
| GET | `/api/v1/admin/stats/materials-by-category` | 素材分类统计 |
| GET | `/api/v1/admin/stats/top-materials` | 热门素材排行 |
| GET | `/api/v1/admin/stats/user-activity` | 用户活跃度 |
| GET | `/api/v1/admin/materials` | 素材管理列表 |
| PUT | `/api/v1/admin/materials/{id}/review` | 素材审核 |
| DELETE | `/api/v1/admin/materials/{id}` | 删除素材 |
| GET | `/api/v1/admin/tags` | 标签管理列表 |
| POST | `/api/v1/admin/tags` | 创建标签 |
| PUT | `/api/v1/admin/tags/{id}` | 修改标签 |
| DELETE | `/api/v1/admin/tags/{id}` | 删除标签 |
| GET | `/api/v1/admin/users` | 用户管理列表 |
| PUT | `/api/v1/admin/users/{id}` | 修改用户（角色/状态） |

## 角色权限

| 角色 | 权限范围 |
|------|---------|
| `student` | 浏览、检索、收藏、学习记录 |
| `teacher` | 学生所有权限 + 素材上传/编辑/删除 + 课件导出 |
| `admin` | 全部权限 + 素材审核 + 用户管理 + 标签管理 + 数据仪表盘 |

## 数据模型

11 张数据表：

- **user** — 用户（雪花ID + BCrypt 密码 + 三角色）
- **material** — 素材（核心表，全文索引）
- **material_attachment** — 素材附件（Word/PDF）
- **tag** — 标签字典
- **material_tag** — 素材-标签多对多关联
- **knowledge_point** — 知识点
- **favorite** — 用户收藏
- **learning_record** — 学习记录
- **search_log** — 搜索日志
- **download_log** — 下载/导出日志
- **category_dict** — 分类字典（朝代9 + 文化品类8 + 学段5）

## 常见问题

### 端口 8089 被占用

如果启动时报错 `Port 8089 was already in use`，说明上次运行未正常关闭，端口仍被占用。

```bash
# Windows（PowerShell）
netstat -ano | findstr 8089          # 查找占用端口的 PID
taskkill //PID <PID> //F              # 杀掉该进程

# Windows（CMD）
netstat -ano | findstr 8089
taskkill /PID <PID> /F

# Linux / macOS
lsof -i :8089                         # 查找占用进程
kill -9 <PID>                         # 杀掉该进程
```

### 数据库连接失败

确认 MySQL 已启动且端口为 `3307`，数据库 `szxx_system` 已创建：

```bash
mysql -uroot -P3307 -e "SHOW DATABASES LIKE 'szxx_system';"
```

## 许可证

内部项目，仅供教学使用。
