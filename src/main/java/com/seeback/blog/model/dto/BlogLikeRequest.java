package com.seeback.blog.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 点赞/取消点赞请求
 */
@Data
public class BlogLikeRequest implements Serializable {

    private static final long serialVersionUID = -7215740212790628669L;

    /**
     * 文章 id
     */
    private Long id;
}
