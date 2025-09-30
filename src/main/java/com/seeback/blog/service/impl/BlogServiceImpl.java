package com.seeback.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.seeback.blog.exception.BusinessException;
import com.seeback.blog.exception.ErrorCode;
import com.seeback.blog.exception.ThrowUtils;
import com.seeback.blog.mapper.BlogDraftMapper;
import com.seeback.blog.mapper.BlogPostMapper;
import com.seeback.blog.model.dto.BlogAddRequest;
import com.seeback.blog.model.dto.BlogDraftSaveRequest;
import com.seeback.blog.model.dto.BlogQueryRequest;
import com.seeback.blog.model.dto.BlogUpdateRequest;
import com.seeback.blog.model.dto.BlogViewRequest;
import com.seeback.blog.model.entity.BlogDraft;
import com.seeback.blog.model.entity.BlogPost;
import com.seeback.blog.model.entity.User;
import com.seeback.blog.model.enums.UserRoleEnum;
import com.seeback.blog.model.vo.BlogAuthorVO;
import com.seeback.blog.model.vo.BlogDraftVO;
import com.seeback.blog.model.vo.BlogStatsVO;
import com.seeback.blog.model.vo.BlogSummaryVO;
import com.seeback.blog.model.vo.BlogVO;
import com.seeback.blog.model.vo.CategoryVO;
import com.seeback.blog.model.vo.TagVO;
import com.seeback.blog.service.BlogService;
import com.seeback.blog.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 博客服务实现
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogPostMapper, BlogPost> implements BlogService {

    private static final int MAX_TAG_SIZE = 10;
    private static final int SUMMARY_MAX_LENGTH = 180;

    @Resource
    private BlogDraftMapper blogDraftMapper;

    @Resource
    private UserService userService;

    @Override
    public BlogVO createBlog(BlogAddRequest request, User currentUser) {
        validateAddRequest(request);
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(request.getTitle().trim());
        blogPost.setContent(request.getContent());
        blogPost.setSummary(generateSummary(request.getSummary(), request.getContent()));
        blogPost.setCategory(StrUtil.blankToDefault(request.getCategory(), "未分类"));
        blogPost.setTags(serializeTags(request.getTags()));
        blogPost.setCover(request.getCover());
        blogPost.setIsPublic(request.getIsPublic() == null || Boolean.TRUE.equals(request.getIsPublic()));
        blogPost.setAllowComments(request.getAllowComments() == null || Boolean.TRUE.equals(request.getAllowComments()));
        blogPost.setViewCount(0L);
        blogPost.setLikeCount(0L);
        blogPost.setAuthorId(currentUser.getId());
        blogPost.setCreateTime(LocalDateTime.now());
        blogPost.setUpdateTime(LocalDateTime.now());
        boolean saved = this.save(blogPost);
        ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR, "保存文章失败");
        return toBlogVO(blogPost, currentUser);
    }

    @Override
    public BlogVO updateBlog(BlogUpdateRequest request, User currentUser) {
        validateUpdateRequest(request);
        BlogPost blogPost = this.getById(request.getId());
        ThrowUtils.throwIf(blogPost == null, ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        ThrowUtils.throwIf(!isAuthorOrAdmin(blogPost, currentUser), ErrorCode.NO_AUTH_ERROR, "无权修改该文章");
        if (StrUtil.isNotBlank(request.getTitle())) {
            blogPost.setTitle(request.getTitle().trim());
        }
        if (StrUtil.isNotBlank(request.getContent())) {
            blogPost.setContent(request.getContent());
        }
        blogPost.setSummary(generateSummary(request.getSummary(), blogPost.getContent()));
        if (request.getCategory() != null) {
            blogPost.setCategory(StrUtil.blankToDefault(request.getCategory(), "未分类"));
        }
        if (request.getTags() != null) {
            blogPost.setTags(serializeTags(request.getTags()));
        }
        blogPost.setCover(request.getCover());
        if (request.getIsPublic() != null) {
            blogPost.setIsPublic(request.getIsPublic());
        }
        if (request.getAllowComments() != null) {
            blogPost.setAllowComments(request.getAllowComments());
        }
        blogPost.setUpdateTime(LocalDateTime.now());
        boolean updated = this.updateById(blogPost);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新文章失败");
        return toBlogVO(blogPost, currentUser);
    }

    @Override
    public boolean deleteBlog(Long id, User currentUser) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "文章 id 非法");
        BlogPost blogPost = this.getById(id);
        ThrowUtils.throwIf(blogPost == null, ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        ThrowUtils.throwIf(!isAuthorOrAdmin(blogPost, currentUser), ErrorCode.NO_AUTH_ERROR, "无权删除该文章");
        return this.removeById(id);
    }

    @Override
    public BlogVO getBlogDetail(Long id, User currentUser) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "文章 id 非法");
        BlogPost blogPost = this.getById(id);
        ThrowUtils.throwIf(blogPost == null, ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        if (!Boolean.TRUE.equals(blogPost.getIsPublic())) {
            ThrowUtils.throwIf(currentUser == null || !isAuthorOrAdmin(blogPost, currentUser),
                    ErrorCode.NO_AUTH_ERROR, "仅作者或管理员可查看草稿");
        }
        return toBlogVO(blogPost, null);
    }

    @Override
    public Page<BlogSummaryVO> pageBlog(BlogQueryRequest request, boolean allowPrivate) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        long pageNum = Math.max(1, request.getPageNum());
        long pageSize = Math.max(1, Math.min(100, request.getPageSize()));
        QueryWrapper queryWrapper = buildQueryWrapper(request, allowPrivate);
        Page<BlogPost> page = this.page(Page.of(pageNum, pageSize), queryWrapper);
        List<BlogSummaryVO> records = toSummaryVOList(page.getRecords());
        Page<BlogSummaryVO> result = new Page<>(pageNum, pageSize, page.getTotalRow());
        result.setRecords(records);
        return result;
    }

    @Override
    public List<BlogSummaryVO> listLatest(int limit) {
        int realLimit = limit > 0 ? Math.min(limit, 20) : 5;
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("isPublic", true)
                .orderBy("createTime", false)
                .limit(realLimit);
        List<BlogPost> list = this.list(queryWrapper);
        return toSummaryVOList(list);
    }

    @Override
    public boolean increaseViewCount(BlogViewRequest request) {
        ThrowUtils.throwIf(request == null || request.getId() == null, ErrorCode.PARAMS_ERROR);
        BlogPost blogPost = this.getById(request.getId());
        ThrowUtils.throwIf(blogPost == null, ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        blogPost.setViewCount(Optional.ofNullable(blogPost.getViewCount()).orElse(0L) + 1);
        blogPost.setUpdateTime(LocalDateTime.now());
        return this.updateById(blogPost);
    }

    @Override
    public boolean likeBlog(Long blogId) {
        ThrowUtils.throwIf(blogId == null || blogId <= 0, ErrorCode.PARAMS_ERROR, "文章 id 非法");
        BlogPost blogPost = this.getById(blogId);
        ThrowUtils.throwIf(blogPost == null, ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        long current = Optional.ofNullable(blogPost.getLikeCount()).orElse(0L);
        blogPost.setLikeCount(current + 1);
        blogPost.setUpdateTime(LocalDateTime.now());
        boolean updated = this.updateById(blogPost);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "点赞失败");
        return true;
    }

    @Override
    public boolean unlikeBlog(Long blogId) {
        ThrowUtils.throwIf(blogId == null || blogId <= 0, ErrorCode.PARAMS_ERROR, "文章 id 非法");
        BlogPost blogPost = this.getById(blogId);
        ThrowUtils.throwIf(blogPost == null, ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        long current = Optional.ofNullable(blogPost.getLikeCount()).orElse(0L);
        ThrowUtils.throwIf(current <= 0, ErrorCode.OPERATION_ERROR, "点赞数异常");
        blogPost.setLikeCount(current - 1);
        blogPost.setUpdateTime(LocalDateTime.now());
        boolean updated = this.updateById(blogPost);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "取消点赞失败");
        return true;
    }

    @Override
    public List<CategoryVO> listCategories() {
        List<BlogPost> posts = this.list(QueryWrapper.create().eq("isPublic", true));
        Map<String, Long> counter = posts.stream()
                .filter(post -> StrUtil.isNotBlank(post.getCategory()))
                .collect(Collectors.groupingBy(BlogPost::getCategory, Collectors.counting()));
        return counter.entrySet().stream()
                .map(entry -> {
                    CategoryVO vo = new CategoryVO();
                    vo.setName(entry.getKey());
                    vo.setCount(entry.getValue());
                    return vo;
                })
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TagVO> listTags() {
        List<BlogPost> posts = this.list(QueryWrapper.create().eq("isPublic", true));
        Map<String, Long> tagCounter = new ConcurrentHashMap<>();
        for (BlogPost post : posts) {
            for (String tag : parseTags(post.getTags())) {
                tagCounter.merge(tag, 1L, Long::sum);
            }
        }
        return tagCounter.entrySet().stream()
                .map(entry -> {
                    TagVO vo = new TagVO();
                    vo.setName(entry.getKey());
                    vo.setCount(entry.getValue());
                    return vo;
                })
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }

    @Override
    public BlogDraftVO saveDraft(BlogDraftSaveRequest request, User currentUser) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(currentUser == null, ErrorCode.NOT_LOGIN_ERROR);
        BlogDraft draft = Optional.ofNullable(request.getId())
                .map(id -> {
                    BlogDraft exist = blogDraftMapper.selectOneById(id);
                    ThrowUtils.throwIf(exist == null, ErrorCode.NOT_FOUND_ERROR, "草稿不存在");
                    ThrowUtils.throwIf(!exist.getAuthorId().equals(currentUser.getId()),
                            ErrorCode.NO_AUTH_ERROR, "无权操作该草稿");
                    return exist;
                }).orElseGet(BlogDraft::new);
        draft.setTitle(request.getTitle());
        draft.setContent(request.getContent());
        draft.setSummary(generateSummary(request.getSummary(), request.getContent()));
        draft.setCategory(request.getCategory());
        draft.setTags(serializeTags(request.getTags()));
        draft.setCover(request.getCover());
        draft.setAuthorId(currentUser.getId());
        LocalDateTime now = LocalDateTime.now();
        if (draft.getId() == null) {
            draft.setCreateTime(now);
        }
        draft.setUpdateTime(now);
        boolean success;
        if (draft.getId() == null) {
            success = blogDraftMapper.insertSelective(draft) > 0;
        } else {
            success = blogDraftMapper.update(draft) > 0;
        }
        ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "保存草稿失败");
        return toDraftVO(draft);
    }

    @Override
    public List<BlogDraftVO> listDrafts(User currentUser) {
        ThrowUtils.throwIf(currentUser == null, ErrorCode.NOT_LOGIN_ERROR);
        List<BlogDraft> drafts = blogDraftMapper.selectListByQuery(QueryWrapper.create()
                .eq("authorId", currentUser.getId())
                .orderBy("updateTime", false));
        return drafts.stream().map(this::toDraftVO).collect(Collectors.toList());
    }

    @Override
    public BlogStatsVO getBlogStats() {
        List<BlogPost> posts = this.list();
        BlogStatsVO stats = new BlogStatsVO();
        stats.setTotalPosts((long) posts.size());
        stats.setTotalViews(posts.stream()
                .map(BlogPost::getViewCount)
                .filter(count -> count != null)
                .reduce(0L, Long::sum));
        Set<String> tags = posts.stream()
                .flatMap(post -> parseTags(post.getTags()).stream())
                .collect(Collectors.toSet());
        stats.setTotalTags((long) tags.size());
        Set<String> categories = posts.stream()
                .map(BlogPost::getCategory)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
        stats.setTotalCategories((long) categories.size());
        LocalDate earliest = posts.stream()
                .map(BlogPost::getCreateTime)
                .filter(dt -> dt != null)
                .map(LocalDateTime::toLocalDate)
                .min(LocalDate::compareTo)
                .orElse(null);
        if (earliest == null) {
            stats.setRunningDays(0L);
        } else {
            long days = ChronoUnit.DAYS.between(earliest, LocalDate.now()) + 1;
            stats.setRunningDays(Math.max(days, 1));
        }
        return stats;
    }

    private void validateAddRequest(BlogAddRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(request.getTitle()), ErrorCode.PARAMS_ERROR, "标题不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(request.getContent()), ErrorCode.PARAMS_ERROR, "内容不能为空");
    }

    private void validateUpdateRequest(BlogUpdateRequest request) {
        ThrowUtils.throwIf(request == null || request.getId() == null, ErrorCode.PARAMS_ERROR);
        if (request.getTags() != null) {
            ThrowUtils.throwIf(request.getTags().size() > MAX_TAG_SIZE, ErrorCode.PARAMS_ERROR, "标签数量过多");
        }
    }

    private QueryWrapper buildQueryWrapper(BlogQueryRequest request, boolean allowPrivate) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("id", request.getId());
        if (!allowPrivate) {
            wrapper.eq("isPublic", true);
        } else if (request.getIsPublic() != null) {
            wrapper.eq("isPublic", request.getIsPublic());
        }
        wrapper.eq("authorId", request.getAuthorId());
        wrapper.eq("category", request.getCategory());
        wrapper.like("title", request.getTitle());
        if (CollUtil.isNotEmpty(request.getTags())) {
            for (String tag : request.getTags()) {
                if (StrUtil.isNotBlank(tag)) {
                    wrapper.like("tags", StrUtil.format("\"{}\"", tag.trim()));
                }
            }
        }
        String sortField = request.getSortField();
        String sortOrder = request.getSortOrder();
        boolean asc = "ascend".equalsIgnoreCase(sortOrder);
        wrapper.orderBy(StrUtil.blankToDefault(sortField, "createTime"), asc);
        return wrapper;
    }

    private BlogVO toBlogVO(BlogPost blogPost, User cachedAuthor) {
        BlogVO vo = new BlogVO();
        vo.setId(blogPost.getId());
        vo.setTitle(blogPost.getTitle());
        vo.setContent(blogPost.getContent());
        vo.setSummary(blogPost.getSummary());
        vo.setCategory(blogPost.getCategory());
        vo.setTags(parseTags(blogPost.getTags()));
        vo.setCover(blogPost.getCover());
        vo.setIsPublic(blogPost.getIsPublic());
        vo.setAllowComments(blogPost.getAllowComments());
        vo.setViewCount(Optional.ofNullable(blogPost.getViewCount()).orElse(0L));
        vo.setLikeCount(Optional.ofNullable(blogPost.getLikeCount()).orElse(0L));
        vo.setReadTime(estimateReadTime(blogPost.getContent()));
        vo.setCreateTime(blogPost.getCreateTime());
        vo.setUpdateTime(blogPost.getUpdateTime());
        vo.setAuthor(buildAuthorVO(blogPost.getAuthorId(), cachedAuthor));
        return vo;
    }

    private List<BlogSummaryVO> toSummaryVOList(List<BlogPost> posts) {
        if (CollUtil.isEmpty(posts)) {
            return Collections.emptyList();
        }
        Set<Long> authorIds = posts.stream()
                .map(BlogPost::getAuthorId)
                .collect(Collectors.toSet());
        Map<Long, User> userMap = userService.listByIds(new ArrayList<>(authorIds)).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        return posts.stream().map(post -> {
            BlogSummaryVO vo = new BlogSummaryVO();
            vo.setId(post.getId());
            vo.setTitle(post.getTitle());
            vo.setSummary(post.getSummary());
            vo.setCategory(post.getCategory());
            vo.setTags(parseTags(post.getTags()));
            vo.setCover(post.getCover());
            vo.setViewCount(Optional.ofNullable(post.getViewCount()).orElse(0L));
            vo.setLikeCount(Optional.ofNullable(post.getLikeCount()).orElse(0L));
            vo.setReadTime(estimateReadTime(post.getContent()));
            vo.setCreateTime(post.getCreateTime());
            vo.setAuthor(buildAuthorVO(post.getAuthorId(), userMap.get(post.getAuthorId())));
            return vo;
        }).collect(Collectors.toList());
    }

    private BlogAuthorVO buildAuthorVO(Long authorId, User cached) {
        User user = cached != null ? cached : userService.getById(authorId);
        if (user == null) {
            return null;
        }
        BlogAuthorVO authorVO = new BlogAuthorVO();
        authorVO.setId(user.getId());
        authorVO.setUserName(user.getUserName());
        authorVO.setUserAvatar(user.getUserAvatar());
        return authorVO;
    }

    private int estimateReadTime(String content) {
        if (StrUtil.isBlank(content)) {
            return 1;
        }
        int chineseCharCount = content.replaceAll("\\s", "").length();
        int readTime = (int) Math.ceil(chineseCharCount / 600.0);
        return Math.max(readTime, 1);
    }

    private List<String> parseTags(String tagsJson) {
        if (StrUtil.isBlank(tagsJson)) {
            return Collections.emptyList();
        }
        return JSONUtil.parseArray(tagsJson).toList(String.class).stream()
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }

    private String serializeTags(List<String> tags) {
        if (CollUtil.isEmpty(tags)) {
            return null;
        }
        List<String> normalized = tags.stream()
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .distinct()
                .limit(MAX_TAG_SIZE)
                .toList();
        if (normalized.isEmpty()) {
            return null;
        }
        return JSONUtil.toJsonStr(normalized);
    }

    private String generateSummary(String summary, String content) {
        if (StrUtil.isNotBlank(summary)) {
            return limitText(summary);
        }
        if (StrUtil.isBlank(content)) {
            return "";
        }
        String plain = content.replaceAll("[#*`>\\-]", " ").replaceAll("\\s+", " ").trim();
        return limitText(plain);
    }

    private String limitText(String text) {
        if (StrUtil.isBlank(text)) {
            return "";
        }
        String trimmed = StrUtil.trim(text);
        if (trimmed.length() <= SUMMARY_MAX_LENGTH) {
            return trimmed;
        }
        return StrUtil.sub(trimmed, 0, SUMMARY_MAX_LENGTH);
    }

    private boolean isAuthorOrAdmin(BlogPost blogPost, User currentUser) {
        if (currentUser == null) {
            return false;
        }
        if (blogPost.getAuthorId() != null && blogPost.getAuthorId().equals(currentUser.getId())) {
            return true;
        }
        return UserRoleEnum.ADMIN.getValue().equals(currentUser.getUserRole());
    }

    private BlogDraftVO toDraftVO(BlogDraft draft) {
        BlogDraftVO vo = new BlogDraftVO();
        vo.setId(draft.getId());
        vo.setTitle(draft.getTitle());
        vo.setContent(draft.getContent());
        vo.setSummary(draft.getSummary());
        vo.setCategory(draft.getCategory());
        vo.setTags(parseTags(draft.getTags()));
        vo.setCover(draft.getCover());
        vo.setUpdateTime(draft.getUpdateTime());
        return vo;
    }
}
