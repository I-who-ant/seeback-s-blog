package com.seeback.blog.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 草稿视图对象
 */
@Data
public class BlogDraftVO implements Serializable {

    private static final long serialVersionUID = -2325536078453906272L;

    private Long id;

    private String title;

    private String content;

    private String summary;

    private String category;

    private List<String> tags;

    private String cover;

    private LocalDateTime updateTime;
}
