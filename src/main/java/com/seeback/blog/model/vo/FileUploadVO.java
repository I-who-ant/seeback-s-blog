package com.seeback.blog.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片上传返回视图
 */
@Data
public class FileUploadVO implements Serializable {

    private static final long serialVersionUID = 5100479321689032860L;

    private String url;

    private String filename;

    private Long size;
}
