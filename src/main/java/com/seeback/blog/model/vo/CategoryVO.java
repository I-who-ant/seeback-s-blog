package com.seeback.blog.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类视图
 */
@Data
public class CategoryVO implements Serializable {

    private static final long serialVersionUID = -8907052000196115249L;

    private String name;

    private Long count;
}
