# blog

基于 Spring Boot + MyBatis Flex 的博客后端服务。

## 功能概览
- 用户注册、登录、鉴权与基础用户管理
- 博客文章增删改查、分页检索、最新文章、浏览量与点赞统计
- 分类 / 标签聚合统计与博客运行统计
- 草稿保存与管理、文章浏览量记录

## 快速开始
```bash
./mvnw spring-boot:run
```

默认服务地址：`http://localhost:8123/api`

## 图片上传配置
- 项目默认使用 sm.ms 图床。
- 运行前设置环境变量：
  ```bash
  export SMMS_TOKEN="dYiv8m2NU5nrkUNhofVye1Lvo1yTpCLN"
  ```
- 可在 `application.yml` 中调整最大体积、允许的图片类型等参数。
- 封面上传接口：`POST /api/blog/cover/upload`，支持上传文件（字段 `file`）或直接注入图片链接（字段 `url`），返回封面 URL 后写入文章。
