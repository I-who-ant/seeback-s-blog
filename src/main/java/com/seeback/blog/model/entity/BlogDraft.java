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
 * 博客草稿实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("blog_draft")
public class BlogDraft implements Serializable {

    @Serial
    private static final long serialVersionUID = -4608506846844763046L;

    /**
     * 主键 id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 草稿标题
     */
    @Column("title")
    private String title;

    /**
     * 草稿内容
     */
    @Column("content")
    private String content;

    /**
     * 摘要
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
}
