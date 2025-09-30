package com.seeback.blog.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 标签视图
 */
@Data
public class TagVO implements Serializable {

    private static final long serialVersionUID = -3177586222064389404L;

    private String name;

    private Long count;
}
