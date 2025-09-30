package com.seeback.blog.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章作者信息
 */
@Data
public class BlogAuthorVO implements Serializable {

    private static final long serialVersionUID = -5426898366091627266L;

    private Long id;

    private String userName;

    private String userAvatar;
}
