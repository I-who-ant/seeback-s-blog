# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## MANDATORY TOOL STRATEGY
═════════════════════════

任务开始前必须执行：
1. memory 查询相关概念
2. code-search 查找代码片段
3. sequential-thinking 分析问题
4. 选择合适子代理

任务结束后必须执行：
1. memory 存储重要概念
2. code-search 存储代码片段
3. 知识总结归档


## Project Overview

This is a Spring Boot project named `blog`. It's a backend service that appears to manage user data, based on the `create_table.sql` file. The project uses Java 21, Maven for build management, and MySQL as its database. It also includes `knife4j` for API documentation.

## Common Commands

- **Build the project**:
  ```bash
  ./mvnw clean package
  ```
- **Run the project**:
  ```bash
  ./mvnw spring-boot:run
  ```
- **Run tests**:
  ```bash
  ./mvnw test
  ```

## Code Architecture

- **Application Entrypoint**: `src/main/java/com/seeback/blog/BlogApplication.java` is the main class that starts the Spring Boot application.
- **Configuration**: `src/main/resources/application.yml` contains the main configuration for the application, including database connection details and server port.
- **Controllers**: The `src/main/java/com/seeback/blog/controller/` package contains REST API controllers. `HealthController.java` is a sample health check endpoint.
- **Database Schema**: The database schema is defined in `sql/create_table.sql`. It currently contains a `user` table.
- **Common Classes**: The `src/main/java/com/seeback/blog/common/` package contains common classes like `BaseResponse`, `PageRequest`, etc., which are used for structuring API responses and requests.
- **Exceptions**: The `src/main/java/com/seeback/blog/exception/` package contains custom exception classes and a global exception handler.
- **ORM Framework**: The project uses `MyBatis-Flex` for database operations.
