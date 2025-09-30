# 开发环境常用访问地址（本地）

本文档记录项目在本地开发运行时的常用访问地址与快速定位方法，便于日常调试与排查。

## 启动与基础信息
- 启动命令（推荐）：`./mvnw spring-boot:run`
- 服务端口：`8123`（来源：`src/main/resources/application.yml:15`）
- 上下文路径（Context Path）：`/api`（来源：`src/main/resources/application.yml:17`）
- 基础访问前缀：`http://localhost:8123/api`

## API 文档入口
- Knife4j UI：`http://localhost:8123/api/doc.html`
- Swagger UI：`http://localhost:8123/api/swagger-ui/index.html`
- OpenAPI JSON（默认分组）：`http://localhost:8123/api/v3/api-docs/default`
- OpenAPI JSON（根入口）：`http://localhost:8123/api/v3/api-docs`

## 项目激活serena
Activate the current dir as project using serena


说明：
- 项目已引入依赖：`knife4j-openapi3-jakarta-spring-boot-starter:4.5.0`
- 文档扫描包（分组配置）：见 `src/main/resources/application.yml:21`
  - `springdoc.group-configs[0].group: default`
  - `springdoc.group-configs[0].packages-to-scan: com.seeback.blog.controller`

## 常用接口示例
- 健康检查：`GET http://localhost:8123/api/health/check`
- 用户接口（示例）：
  - `GET    /user/list`
  - `GET    /user/getInfo/{id}`
  - `POST   /user/save`
  - `PUT    /user/update`
  - `DELETE /user/remove/{id}`
- 博客接口（核心）：
  - `POST   /blog/add`
  - `POST   /blog/update`
  - `POST   /blog/delete`
  - `POST   /blog/list/page/vo`
  - `GET    /blog/get/vo?id={id}`
  - `GET    /blog/latest?limit=5`
  - `POST   /blog/view`
  - `POST   /blog/like`（基于 Cookie 防重复，匿名可用）
  - `POST   /blog/unlike`（需已有 `blog_like_{id}` Cookie）
  - `GET    /blog/categories`
  - `GET    /blog/tags`
  - `POST   /blog/draft/save`
  - `GET    /blog/draft/list`
  - `POST   /blog/cover/upload`（FormData：`file` 上传图片或 `url` 直接使用外链）
  - `GET    /blog/stats`
- 文件接口：
  - `POST   /file/upload/image`

完整路径示例：`http://localhost:8123/api/user/list`

## 常见问题排查
1) 启动日志不显示完整 URL？
- Spring Boot 仅打印端口与 Context Path，例如：`Tomcat started on port(s): 8123 (http) with context path '/api'`。
- 访问时请自行拼接：`http://localhost:8123/api`。

2) 访问 `doc.html`/`swagger-ui` 404？
- 确认依赖存在：`pom.xml` 中包含 Knife4j 依赖。
- 确认上下文配置：端口与 `context-path` 是否与上述一致。
- 确认扫描包：`application.yml` 中 `packages-to-scan` 是否为 `com.seeback.blog.controller`。
- 清浏览器缓存或使用无痕窗口再次访问。

3) 文档无接口项？
- 确认控制器类均在 `com.seeback.blog.controller` 包下，且有 `@RestController`/`@RequestMapping`。
- 若使用分组，确认访问 `v3/api-docs/default` 是否能返回 JSON。

## 变更配置指引
- 修改端口：编辑 `src/main/resources/application.yml:15` 中的 `server.port`
- 修改上下文路径：编辑 `src/main/resources/application.yml:17` 中的 `server.servlet.context-path`
- 修改文档扫描包：编辑 `src/main/resources/application.yml:21` 下的 `springdoc.group-configs[0].packages-to-scan`
- 配置图床：`storage.smms.token` 建议通过环境变量 `SMMS_TOKEN` 设置，不要硬编码。

## 参考文件
- 应用配置：`src/main/resources/application.yml`
- 控制器包：`src/main/java/com/seeback/blog/controller`
- 启动类：`src/main/java/com/seeback/blog/BlogApplication.java`

---
备注：以上地址基于本地默认配置，如有环境差异（端口/上下文/反向代理），请以实际配置为准。

