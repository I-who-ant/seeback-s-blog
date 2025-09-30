package com.seeback.blog.controller;

import com.mybatisflex.core.paginate.Page;
import com.seeback.blog.common.BaseResponse;
import com.seeback.blog.common.DeleteRequest;
import com.seeback.blog.common.ResultUtils;
import com.seeback.blog.exception.BusinessException;
import com.seeback.blog.exception.ErrorCode;
import com.seeback.blog.exception.ThrowUtils;
import com.seeback.blog.model.dto.BlogAddRequest;
import com.seeback.blog.model.dto.BlogDraftSaveRequest;
import com.seeback.blog.model.dto.BlogLikeRequest;
import com.seeback.blog.model.dto.BlogQueryRequest;
import com.seeback.blog.model.dto.BlogUpdateRequest;
import com.seeback.blog.model.dto.BlogViewRequest;
import com.seeback.blog.model.entity.User;
import com.seeback.blog.model.enums.UserRoleEnum;
import com.seeback.blog.model.vo.BlogDraftVO;
import com.seeback.blog.model.vo.BlogStatsVO;
import com.seeback.blog.model.vo.BlogSummaryVO;
import com.seeback.blog.model.vo.BlogVO;
import com.seeback.blog.model.vo.CategoryVO;
import com.seeback.blog.model.vo.FileUploadVO;
import com.seeback.blog.model.vo.TagVO;
import com.seeback.blog.service.BlogService;
import com.seeback.blog.service.FileService;
import com.seeback.blog.service.UserService;
import jakarta.annotation.Resource;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 博客相关接口
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private BlogService blogService;

    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    @PostMapping("/add")
    public BaseResponse<BlogVO> addBlog(@RequestBody BlogAddRequest request, HttpServletRequest httpRequest) {
        User currentUser = userService.getLoginUser(httpRequest);
        BlogVO blogVO = blogService.createBlog(request, currentUser);
        return ResultUtils.success(blogVO);
    }

    @PostMapping("/update")
    public BaseResponse<BlogVO> updateBlog(@RequestBody BlogUpdateRequest request, HttpServletRequest httpRequest) {
        User currentUser = userService.getLoginUser(httpRequest);
        BlogVO blogVO = blogService.updateBlog(request, currentUser);
        return ResultUtils.success(blogVO);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteBlog(@RequestBody DeleteRequest deleteRequest, HttpServletRequest httpRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        User currentUser = userService.getLoginUser(httpRequest);
        boolean result = blogService.deleteBlog(deleteRequest.getId(), currentUser);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<BlogVO> getBlogDetail(@RequestParam("id") Long id, HttpServletRequest httpRequest) {
        User currentUser = tryGetLoginUser(httpRequest);
        BlogVO blogVO = blogService.getBlogDetail(id, currentUser);
        return ResultUtils.success(blogVO);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<BlogSummaryVO>> listBlogPage(@RequestBody BlogQueryRequest request, HttpServletRequest httpRequest) {
        User currentUser =
                tryGetLoginUser(httpRequest);
        boolean allowPrivate = currentUser != null && (UserRoleEnum.ADMIN.getValue().equals(currentUser.getUserRole())
                || (request.getAuthorId() != null && request.getAuthorId().equals(currentUser.getId())));
        Page<BlogSummaryVO> page = blogService.pageBlog(request, allowPrivate);
        return ResultUtils.success(page);
    }

    @GetMapping("/latest")
    public BaseResponse<List<BlogSummaryVO>> listLatest(@RequestParam(value = "limit", defaultValue = "5") int limit) {
        List<BlogSummaryVO> latest = blogService.listLatest(limit);
        return ResultUtils.success(latest);
    }

    @PostMapping("/view")
    public BaseResponse<Boolean> increaseView(@RequestBody BlogViewRequest request) {
        boolean result = blogService.increaseViewCount(request);
        return ResultUtils.success(result);
    }

    @PostMapping("/like")
    public BaseResponse<Boolean> likeBlog(@RequestBody BlogLikeRequest request,
                                          HttpServletRequest httpRequest,
                                          HttpServletResponse httpResponse) {
        ThrowUtils.throwIf(request == null || request.getId() == null, ErrorCode.PARAMS_ERROR);
        String cookieName = buildLikeCookieName(request.getId());
        ThrowUtils.throwIf(hasLikeCookie(httpRequest, cookieName), ErrorCode.OPERATION_ERROR, "请勿重复点赞");
        boolean result = blogService.likeBlog(request.getId());
        if (result) {
            Cookie cookie = new Cookie(cookieName, "1");
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 365);
            cookie.setHttpOnly(false);
            httpResponse.addCookie(cookie);
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/unlike")
    public BaseResponse<Boolean> unlikeBlog(@RequestBody BlogLikeRequest request,
                                            HttpServletRequest httpRequest,
                                            HttpServletResponse httpResponse) {
        ThrowUtils.throwIf(request == null || request.getId() == null, ErrorCode.PARAMS_ERROR);
        String cookieName = buildLikeCookieName(request.getId());
        ThrowUtils.throwIf(!hasLikeCookie(httpRequest, cookieName), ErrorCode.OPERATION_ERROR, "尚未点赞");
        boolean result = blogService.unlikeBlog(request.getId());
        if (result) {
            Cookie cookie = new Cookie(cookieName, "");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setHttpOnly(false);
            httpResponse.addCookie(cookie);
        }
        return ResultUtils.success(result);
    }

    @GetMapping("/categories")
    public BaseResponse<List<CategoryVO>> listCategories() {
        return ResultUtils.success(blogService.listCategories());
    }

    @GetMapping("/tags")
    public BaseResponse<List<TagVO>> listTags() {
        return ResultUtils.success(blogService.listTags());
    }

    @PostMapping("/draft/save")
    public BaseResponse<BlogDraftVO> saveDraft(@RequestBody BlogDraftSaveRequest request, HttpServletRequest httpRequest) {
        User currentUser = userService.getLoginUser(httpRequest);
        BlogDraftVO draftVO = blogService.saveDraft(request, currentUser);
        return ResultUtils.success(draftVO);
    }

    @GetMapping("/draft/list")
    public BaseResponse<List<BlogDraftVO>> listDrafts(HttpServletRequest httpRequest) {
        User currentUser = userService.getLoginUser(httpRequest);
        List<BlogDraftVO> drafts = blogService.listDrafts(currentUser);
        return ResultUtils.success(drafts);
    }

    @PostMapping("/cover/upload")
    public BaseResponse<FileUploadVO> uploadCover(@RequestPart(value = "file", required = false) MultipartFile file,
                                                  @RequestParam(value = "url", required = false) String coverUrl,
                                                  HttpServletRequest httpRequest) {
        userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf((file == null || file.isEmpty()) && StrUtil.isBlank(coverUrl),
                ErrorCode.PARAMS_ERROR, "请上传图片或提供图片链接");
        FileUploadVO vo;
        if (file != null && !file.isEmpty()) {
            vo = fileService.uploadImage(file);
        } else {
            vo = fileService.useExternalImage(coverUrl);
        }
        return ResultUtils.success(vo);
    }

    @GetMapping("/stats")
    public BaseResponse<BlogStatsVO> stats() {
        return ResultUtils.success(blogService.getBlogStats());
    }

    private User tryGetLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        try {
            return userService.getLoginUser(request);
        } catch (BusinessException ex) {
            if (ex.getCode() == ErrorCode.NOT_LOGIN_ERROR.getCode()) {
                return null;
            }
            throw ex;
        }
    }

    private boolean hasLikeCookie(HttpServletRequest request, String cookieName) {
        if (request == null || request.getCookies() == null) {
            return false;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName()) && StrUtil.isNotBlank(cookie.getValue())) {
                return true;
            }
        }
        return false;
    }

    private String buildLikeCookieName(Long blogId) {
        return "blog_like_" + blogId;
    }
}
