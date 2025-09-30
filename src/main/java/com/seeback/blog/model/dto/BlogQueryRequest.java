package com.seeback.blog.model.dto;

import com.seeback.blog.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 博客文章查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlogQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4471461529461050970L;

    /**
     * 文章 id
     */
    private Long id;

    /**
     * 标题关键词
     */
    private String title;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 作者 id
     */
    private Long authorId;

    /**
     * 是否公开
     */
    private Boolean isPublic;
}
