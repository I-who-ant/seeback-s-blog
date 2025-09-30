package com.seeback.blog.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 草稿保存请求
 */
@Data
public class BlogDraftSaveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -5848748627983090745L;

    /**
     * 草稿 id（存在表示更新）
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
     * 标签
     */
    private List<String> tags;

    /**
     * 封面
     */
    private String cover;
}
