
-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
) comment '用户' collate = utf8mb4_unicode_ci;



-- 文章表
create table if not exists blog_post
(
    id             bigint       not null primary key comment '主键',
    title          varchar(512) not null comment '标题',
    content        longtext     not null comment '内容（Markdown）',
    summary        text         null comment '摘要',
    category       varchar(256) null comment '分类',
    tags           json         null comment '标签 JSON',
    cover          varchar(1024) null comment '封面',
    isPublic       tinyint      default 1 not null comment '是否公开',
    allowComments  tinyint      default 1 not null comment '允许评论',
    viewCount      bigint       default 0 not null comment '浏览量',
    likeCount      bigint       default 0 not null comment '点赞数',
    authorId       bigint       not null comment '作者 id',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint      default 0 not null comment '是否删除',
    index idx_authorId (authorId),
    index idx_category (category),
    index idx_createTime (createTime)
) comment '博客文章';

-- 草稿表
create table if not exists blog_draft
(
    id         bigint       not null primary key comment '主键',
    title      varchar(512) null comment '标题',
    content    longtext     null comment '内容',
    summary    text         null comment '摘要',
    category   varchar(256) null comment '分类',
    tags       json         null comment '标签 JSON',
    cover      varchar(1024) null comment '封面',
    authorId   bigint       not null comment '作者 id',
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_draft_author (authorId)
) comment '博客草稿';

-- 点赞记录表
create table if not exists blog_like
(
    id         bigint      not null primary key comment '主键',
    blogId     bigint      not null comment '文章 id',
    userId     bigint      not null comment '用户 id',
    createTime datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    unique key uk_blog_user (blogId, userId),
    index idx_blog (blogId)
) comment '文章点赞记录';
