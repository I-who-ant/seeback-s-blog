package com.seeback.blog.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章详情视图
 */
@Data
public class BlogVO implements Serializable {

    private static final long serialVersionUID = -3478543824247118245L;

    private Long id;

    private String title;

    private String content;

    private String summary;

    private String category;

    private List<String> tags;

    private String cover;

    private Boolean isPublic;

    private Boolean allowComments;

    private Long viewCount;

    private Long likeCount;

    private Integer readTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private BlogAuthorVO author;
}
