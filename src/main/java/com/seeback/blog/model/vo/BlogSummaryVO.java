package com.seeback.blog.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章列表视图
 */
@Data
public class BlogSummaryVO implements Serializable {

    private static final long serialVersionUID = -4882674212270666620L;

    private Long id;

    private String title;

    private String summary;

    private String category;

    private List<String> tags;

    private String cover;

    private Long viewCount;

    private Long likeCount;

    private Integer readTime;

    private LocalDateTime createTime;

    private BlogAuthorVO author;
}
