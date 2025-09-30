# seebackのblog 后端接口需求文档

## 概述

本文档描述了 seebackのblog 项目的后端接口需求，用于指导后端开发人员实现相应的 API 接口。

## 技术规范

- **接口格式**: RESTful API
- **数据格式**: JSON
- **认证方式**: Cookie-based Session 或 JWT Token
- **状态码**: 遵循 HTTP 标准状态码
- **API 前缀**: `/api`

## 通用响应格式

### 成功响应
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    // 具体数据
  }
}
```

### 错误响应
```json
{
  "code": 40001,
  "message": "错误描述",
  "data": null
}
```

## 用户认证相关接口

### 1. 用户注册
- **接口**: `POST /api/user/register`
- **描述**: 用户注册账号
- **请求参数**:
```json
{
  "userName": "用户名",
  "userPassword": "密码",
  "checkPassword": "确认密码",
  "email": "邮箱地址"
}
```
- **响应数据**:
```json
{
  "id": 1,
  "userName": "用户名",
  "email": "邮箱地址",
  "userRole": "user",
  "createTime": "2024-01-15T10:30:00Z"
}
```

### 2. 用户登录
- **接口**: `POST /api/user/login`
- **描述**: 用户登录
- **请求参数**:
```json
{
  "userName": "用户名",
  "userPassword": "密码"
}
```
- **响应数据**:
```json
{
  "id": 1,
  "userName": "用户名",
  "email": "邮箱地址",
  "userRole": "user",
  "userAvatar": "头像URL",
  "userProfile": "个人简介"
}
```

### 3. 获取当前登录用户
- **接口**: `GET /api/user/get/login`
- **描述**: 获取当前登录用户信息
- **响应数据**: 同登录接口

### 4. 用户登出
- **接口**: `POST /api/user/logout`
- **描述**: 用户登出
- **响应数据**: 标准成功响应

### 5. 更新用户信息
- **接口**: `POST /api/user/update`
- **描述**: 更新用户个人信息
- **请求参数**:
```json
{
  "userName": "新用户名",
  "email": "新邮箱",
  "userAvatar": "新头像URL",
  "userProfile": "新个人简介"
}
```

## 博客文章相关接口

### 1. 发布文章
- **接口**: `POST /api/blog/add`
- **描述**: 发布新文章
- **权限**: 需要登录
- **请求参数**:
```json
{
  "title": "文章标题",
  "content": "文章内容（Markdown格式）",
  "summary": "文章摘要",
  "category": "分类",
  "tags": ["标签1", "标签2"],
  "cover": "封面图片URL",
  "isPublic": true,
  "allowComments": true
}
```
- **响应数据**:
```json
{
  "id": 1,
  "title": "文章标题",
  "summary": "文章摘要",
  "category": "分类",
  "tags": ["标签1", "标签2"],
  "cover": "封面图片URL",
  "isPublic": true,
  "allowComments": true,
  "viewCount": 0,
  "likeCount": 0,
  "createTime": "2024-01-15T10:30:00Z",
  "updateTime": "2024-01-15T10:30:00Z",
  "author": {
    "id": 1,
    "userName": "作者名",
    "userAvatar": "头像URL"
  }
}
```

### 2. 更新文章
- **接口**: `POST /api/blog/update`
- **描述**: 更新文章内容
- **权限**: 需要登录且为文章作者
- **请求参数**: 同发布文章，但需包含文章ID
```json
{
  "id": 1,
  "title": "更新后的标题",
  // ... 其他字段
}
```

### 3. 删除文章
- **接口**: `POST /api/blog/delete`
- **描述**: 删除文章
- **权限**: 需要登录且为文章作者
- **请求参数**:
```json
{
  "id": 1
}
```

### 4. 获取文章详情
- **接口**: `GET /api/blog/get/vo`
- **描述**: 获取文章详细信息
- **请求参数**: `?id=1`
- **响应数据**: 同发布文章响应，但包含完整content字段

### 5. 获取文章列表
- **接口**: `POST /api/blog/list/page/vo`
- **描述**: 分页获取文章列表
- **请求参数**:
```json
{
  "current": 1,
  "pageSize": 10,
  "sortField": "createTime",
  "sortOrder": "descend",
  "category": "技术",
  "tags": ["Vue"],
  "title": "搜索关键词",
  "authorId": 1
}
```
- **响应数据**:
```json
{
  "records": [
    {
      "id": 1,
      "title": "文章标题",
      "summary": "文章摘要",
      "category": "分类",
      "tags": ["标签1", "标签2"],
      "cover": "封面图片URL",
      "viewCount": 100,
      "likeCount": 25,
      "readTime": 8,
      "createTime": "2024-01-15T10:30:00Z",
      "author": {
        "id": 1,
        "userName": "作者名",
        "userAvatar": "头像URL"
      }
    }
  ],
  "total": 50,
  "size": 10,
  "current": 1,
  "pages": 5
}
```

### 6. 获取最新文章
- **接口**: `GET /api/blog/latest`
- **描述**: 获取最新发布的文章列表
- **请求参数**: `?limit=5`
- **响应数据**: 文章列表数组（不分页）

### 7. 保存草稿
- **接口**: `POST /api/blog/draft/save`
- **描述**: 保存文章草稿
- **权限**: 需要登录
- **请求参数**: 同发布文章

### 8. 获取草稿列表
- **接口**: `GET /api/blog/draft/list`
- **描述**: 获取当前用户的草稿列表
- **权限**: 需要登录

## 分类和标签接口

### 1. 获取所有分类
- **接口**: `GET /api/blog/categories`
- **描述**: 获取所有文章分类
- **响应数据**:
```json
[
  {
    "name": "技术",
    "count": 25
  },
  {
    "name": "设计",
    "count": 10
  }
]
```

### 2. 获取所有标签
- **接口**: `GET /api/blog/tags`
- **描述**: 获取所有文章标签
- **响应数据**:
```json
[
  {
    "name": "Vue",
    "count": 15
  },
  {
    "name": "JavaScript",
    "count": 20
  }
]
```

## 统计相关接口

### 1. 增加文章浏览量
- **接口**: `POST /api/blog/view`
- **描述**: 记录文章浏览
- **请求参数**:
```json
{
  "id": 1
}
```

### 2. 获取博客统计信息
- **接口**: `GET /api/blog/stats`
- **描述**: 获取博客整体统计数据
- **响应数据**:
```json
{
  "totalPosts": 23,
  "totalViews": 2856,
  "totalTags": 12,
  "totalCategories": 4,
  "runningDays": 95
}
```

### 3. 点赞 / 取消点赞
- **接口**: `POST /api/blog/like`
- **描述**: 无需登录，基于浏览器 Cookie 记录点赞状态（幂等，不可重复点赞）
- **请求参数**:
```json
{
  "id": 1
}
```
- **接口**: `POST /api/blog/unlike`
- **描述**: 取消点赞（需要已有点赞 Cookie）
- **请求参数**: 与点赞相同
- **响应数据**: 标准成功响应，`data` 为 `true/false`

### 4. 上传封面
- **接口**: `POST /api/blog/cover/upload`
- **描述**: 登录用户上传文章封面图片，内部会存储到 sm.ms 图床并返回访问 URL；也支持直接提交已存在的网络图片链接
- **请求**: Form-data
  - `file`：可选，二选一；上传图片文件
  - `url`：可选，二选一；http/https 的图片链接
- **响应数据**:
```json
{
  "url": "https://...",
  "filename": "cover.png",
  "size": 123456
}
```
```json
{
  "url": "https://example.com/banner.jpg",
  "filename": "banner.jpg",
  "size": null
}
```

## 文件上传接口

### 1. 上传图片
- **接口**: `POST /api/file/upload/image`
- **描述**: 上传图片文件（用于文章封面、内容图片等）
- **请求**: Form-data格式，文件字段名为 `file`
- **响应数据**:
```json
{
  "url": "https://example.com/images/xxx.jpg",
  "filename": "original_filename.jpg",
  "size": 1024000
}
```

## 健康检查接口

### 1. 健康检查
- **接口**: `GET /api/health`
- **描述**: 服务健康状态检查
- **响应数据**:
```json
{
  "status": "ok",
  "timestamp": "2024-01-15T10:30:00Z",
  "version": "1.0.0"
}
```

## 错误码定义

| 错误码 | 描述 |
|--------|------|
| 0 | 操作成功 |
| 40001 | 请求参数错误 |
| 40100 | 未登录 |
| 40101 | 无权限 |
| 40400 | 请求数据不存在 |
| 50000 | 系统内部异常 |
| 50001 | 数据库操作异常 |

## 数据模型建议

### 用户表 (user)
```sql
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_name VARCHAR(256) NOT NULL UNIQUE,
  user_password VARCHAR(512) NOT NULL,
  email VARCHAR(512),
  user_avatar VARCHAR(1024),
  user_profile TEXT,
  user_role VARCHAR(256) DEFAULT 'user',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT DEFAULT 0
);
```

### 文章表 (blog_post)
```sql
CREATE TABLE blog_post (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(512) NOT NULL,
  content LONGTEXT NOT NULL,
  summary TEXT,
  category VARCHAR(256),
  tags JSON,
  cover VARCHAR(1024),
  is_public TINYINT DEFAULT 1,
  allow_comments TINYINT DEFAULT 1,
  view_count INT DEFAULT 0,
  like_count INT DEFAULT 0,
  author_id BIGINT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT DEFAULT 0,
  INDEX idx_author_id (author_id),
  INDEX idx_category (category),
  INDEX idx_create_time (create_time)
);
```

### 草稿表 (blog_draft)
```sql
CREATE TABLE blog_draft (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(512),
  content LONGTEXT,
  summary TEXT,
  category VARCHAR(256),
  tags JSON,
  cover VARCHAR(1024),
  author_id BIGINT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_author_id (author_id)
);
```

## 安全考虑

1. **输入验证**: 所有用户输入都需要进行严格的验证和清理
2. **SQL注入防护**: 使用参数化查询
3. **XSS防护**: 对用户输入的HTML内容进行过滤
4. **文件上传安全**: 限制文件类型、大小，验证文件内容
5. **权限控制**: 严格验证用户权限，确保用户只能操作自己的数据
6. **频率限制**: 对敏感操作添加频率限制

## 性能优化建议

1. **分页查询**: 所有列表接口都应支持分页
2. **缓存策略**: 对热点数据（如文章列表、分类标签）使用缓存
3. **数据库索引**: 为常用查询字段添加索引
4. **图片优化**: 支持图片压缩和多种尺寸
5. **CDN**: 静态资源使用CDN加速

---

*文档版本: v1.0*
*更新时间: 2024-01-15*