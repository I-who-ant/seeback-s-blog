package com.seeback.blog.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 博客统计信息
 */
@Data
public class BlogStatsVO implements Serializable {

    private static final long serialVersionUID = -1108245868209129390L;

    private Long totalPosts;

    private Long totalViews;

    private Long totalTags;

    private Long totalCategories;

    private Long runningDays;
}
