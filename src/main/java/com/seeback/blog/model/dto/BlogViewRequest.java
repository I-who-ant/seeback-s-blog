package com.seeback.blog.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 浏览量上报请求
 */
@Data
public class BlogViewRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 2712386821172501573L;

    /**
     * 文章 id
     */
    private Long id;
}
