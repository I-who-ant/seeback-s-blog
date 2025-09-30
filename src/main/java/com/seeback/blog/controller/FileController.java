package com.seeback.blog.controller;

import com.seeback.blog.common.BaseResponse;
import com.seeback.blog.common.ResultUtils;
import com.seeback.blog.model.vo.FileUploadVO;
import com.seeback.blog.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload/image")
    public BaseResponse<FileUploadVO> uploadImage(@RequestParam("file") MultipartFile file) {
        FileUploadVO result = fileService.uploadImage(file);
        return ResultUtils.success(result);
    }
}
