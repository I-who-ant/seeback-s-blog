package com.seeback.blog.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客文章实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("blog_post")
public class BlogPost implements Serializable {

    @Serial
    private static final long serialVersionUID = -9149446319869309479L;

    /**
     * 主键 id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 文章标题
     */
    @Column("title")
    private String title;

    /**
     * 文章内容（Markdown）
     */
    @Column("content")
    private String content;

    /**
     * 文章摘要
     */
    @Column("summary")
    private String summary;

    /**
     * 分类
     */
    @Column("category")
    private String category;

    /**
     * 标签 JSON
     */
    @Column("tags")
    private String tags;

    /**
     * 封面图
     */
    @Column("cover")
    private String cover;

    /**
     * 是否公开
     */
    @Column("isPublic")
    private Boolean isPublic;

    /**
     * 是否允许评论
     */
    @Column("allowComments")
    private Boolean allowComments;

    /**
     * 浏览量
     */
    @Column("viewCount")
    private Long viewCount;

    /**
     * 点赞数
     */
    @Column("likeCount")
    private Long likeCount;

    /**
     * 作者 id
     */
    @Column("authorId")
    private Long authorId;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column("updateTime")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;
}
