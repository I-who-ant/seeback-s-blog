package com.seeback.blog.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.seeback.blog.model.dto.BlogAddRequest;
import com.seeback.blog.model.dto.BlogDraftSaveRequest;
import com.seeback.blog.model.dto.BlogQueryRequest;
import com.seeback.blog.model.dto.BlogUpdateRequest;
import com.seeback.blog.model.dto.BlogViewRequest;
import com.seeback.blog.model.entity.BlogPost;
import com.seeback.blog.model.entity.User;
import com.seeback.blog.model.vo.BlogDraftVO;
import com.seeback.blog.model.vo.BlogStatsVO;
import com.seeback.blog.model.vo.BlogSummaryVO;
import com.seeback.blog.model.vo.BlogVO;
import com.seeback.blog.model.vo.CategoryVO;
import com.seeback.blog.model.vo.TagVO;

import java.util.List;

/**
 * 博客服务接口
 */
public interface BlogService extends IService<BlogPost> {

    BlogVO createBlog(BlogAddRequest request, User currentUser);

    BlogVO updateBlog(BlogUpdateRequest request, User currentUser);

    boolean deleteBlog(Long id, User currentUser);

    BlogVO getBlogDetail(Long id, User currentUser);

    Page<BlogSummaryVO> pageBlog(BlogQueryRequest request, boolean allowPrivate);

    List<BlogSummaryVO> listLatest(int limit);

    boolean increaseViewCount(BlogViewRequest request);

    boolean likeBlog(Long blogId);

    boolean unlikeBlog(Long blogId);

    List<CategoryVO> listCategories();

    List<TagVO> listTags();

    BlogDraftVO saveDraft(BlogDraftSaveRequest request, User currentUser);

    List<BlogDraftVO> listDrafts(User currentUser);

    BlogStatsVO getBlogStats();
}
