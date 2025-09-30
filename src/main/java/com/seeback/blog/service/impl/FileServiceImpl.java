package com.seeback.blog.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.seeback.blog.exception.BusinessException;
import com.seeback.blog.exception.ErrorCode;
import com.seeback.blog.exception.ThrowUtils;
import com.seeback.blog.model.dto.SmmsUploadResponse;
import com.seeback.blog.model.vo.FileUploadVO;
import com.seeback.blog.service.FileService;

import java.util.Arrays;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 文件服务实现，集成 sm.ms 图床
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    private final RestTemplate restTemplate;

    private static final java.util.List<String> ALLOWED_LINK_SUFFIX = Arrays.asList(
            "jpg", "jpeg", "png", "webp", "gif", "bmp", "svg", "ico"
    );

    @Value("${storage.smms.base-url:https://smms.app/api/v2}")
    private String baseUrl;

    @Value("${storage.smms.token:}")
    private String token;

    @Value("${storage.smms.max-size:5242880}")
    private long maxSize;

    @Value("${storage.smms.allowed-types:image/jpeg,image/png,image/webp,image/gif}")
    private String allowedTypes;

    private List<String> allowedTypeList;

    public FileServiceImpl(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @PostConstruct
    public void init() {
        this.allowedTypeList = Arrays.stream(allowedTypes.split(","))
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .toList();
    }

    @Override
    public FileUploadVO uploadImage(MultipartFile file) {
        ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.PARAMS_ERROR, "请上传图片文件");
        ThrowUtils.throwIf(StrUtil.isBlank(token), ErrorCode.SYSTEM_ERROR, "SMMS Token 未配置，请先设置环境变量 SMMS_TOKEN");
        ThrowUtils.throwIf(file.getSize() > maxSize, ErrorCode.PARAMS_ERROR, "图片体积超过限制");
        String contentType = file.getContentType();
        ThrowUtils.throwIf(StrUtil.isNotBlank(contentType) && !allowedTypeList.contains(contentType),
                ErrorCode.PARAMS_ERROR, "不支持的图片类型");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(HttpHeaders.AUTHORIZATION, "Token " + token.trim());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            body.add("smfile", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
        } catch (IOException e) {
            log.error("读取上传文件失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "读取图片失败");
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String uploadUrl = baseUrl.endsWith("/") ? baseUrl + "upload" : baseUrl + "/upload";
        ResponseEntity<SmmsUploadResponse> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(uploadUrl, requestEntity, SmmsUploadResponse.class);
        } catch (RestClientException ex) {
            log.error("调用 SMMS 上传接口失败", ex);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用图床接口失败");
        }

        SmmsUploadResponse response = responseEntity.getBody();
        if (response == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图床返回空响应");
        }

        if (Boolean.TRUE.equals(response.getSuccess()) && response.getData() != null) {
            SmmsUploadResponse.FileInfo data = response.getData();
            FileUploadVO vo = new FileUploadVO();
            vo.setUrl(data.getUrl());
            vo.setFilename(data.getFilename());
            vo.setSize(data.getSize());
            return vo;
        }

        if ("image_repeated".equals(response.getCode()) && StrUtil.isNotBlank(response.getImages())) {
            FileUploadVO vo = new FileUploadVO();
            vo.setUrl(response.getImages());
            vo.setFilename(file.getOriginalFilename());
            vo.setSize(file.getSize());
            return vo;
        }

        String message = StrUtil.blankToDefault(response.getMessage(), "图片上传失败");
        throw new BusinessException(ErrorCode.OPERATION_ERROR, message);
    }

    @Override
    public FileUploadVO useExternalImage(String url) {
        String normalized = StrUtil.trim(url);
        ThrowUtils.throwIf(StrUtil.isBlank(normalized), ErrorCode.PARAMS_ERROR, "图片链接不能为空");
        ThrowUtils.throwIf(!Validator.isUrl(normalized)
                        || !(StrUtil.startWithIgnoreCase(normalized, "http://")
                        || StrUtil.startWithIgnoreCase(normalized, "https://")),
                ErrorCode.PARAMS_ERROR, "图片链接必须为 http/https 地址");

        String fileName = StrUtil.subAfter(normalized, '/', true);
        if (StrUtil.isBlank(fileName)) {
            fileName = "remote-image";
        }
        if (fileName.contains("?")) {
            fileName = StrUtil.subBefore(fileName, "?", true);
        }
        String ext = StrUtil.emptyToDefault(StrUtil.subAfter(fileName, '.', true), "").toLowerCase();
        ThrowUtils.throwIf(StrUtil.isBlank(ext) || !ALLOWED_LINK_SUFFIX.contains(ext),
                ErrorCode.PARAMS_ERROR, "仅支持常见图片格式（jpeg/png/webp/gif/bmp/svg/ico）链接");
        if (StrUtil.isBlank(fileName)) {
            fileName = "remote-image." + ext;
        }

        FileUploadVO vo = new FileUploadVO();
        vo.setUrl(normalized);
        vo.setFilename(fileName);
        vo.setSize(null);
        return vo;
    }
}
