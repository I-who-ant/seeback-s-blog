package com.seeback.blog.service;

import com.seeback.blog.model.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传图片到图床
     *
     * @param file 图片文件
     * @return 上传结果
     */
    FileUploadVO uploadImage(MultipartFile file);

    /**
     * 直接使用外部图片链接
     *
     * @param url 图片访问地址
     * @return 包装后的图片信息
     */
    FileUploadVO useExternalImage(String url);
}
