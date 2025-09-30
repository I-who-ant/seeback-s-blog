package com.seeback.blog.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 更新博客文章请求
 */
@Data
public class BlogUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 2948147368472632182L;

    /**
     * 主键 id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 封面
     */
    private String cover;

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 是否允许评论
     */
    private Boolean allowComments;
}
