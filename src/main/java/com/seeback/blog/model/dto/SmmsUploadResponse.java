package com.seeback.blog.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * sm.ms 上传响应
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmmsUploadResponse {

    private Boolean success;

    private String code;

    private String message;

    private FileInfo data;

    /**
     * 当图片重复时返回的地址
     */
    private String images;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FileInfo {

        private String url;

        private String hash;

        private String filename;

        private Long size;

        private Integer width;

        private Integer height;

        @JsonProperty("delete")
        private String deleteUrl;
    }
}
