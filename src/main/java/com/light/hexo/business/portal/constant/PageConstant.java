package com.light.hexo.business.portal.constant;

/**
 * @Author MoonlightL
 * @ClassName: PageConstant
 * @ProjectName hexo-boot
 * @Description: 页面常量
 * @DateTime 2020/9/18 17:37
 */
public class PageConstant {

    /**
     * md 转 html 的 key
     */
    public static final String MARKDOWN_KEY = "markdown:key";

    /**
     * 首页列表缓存 key（格式：PageConstant.POST_PAGE + #pageNum）
     */
    public static final String POST_PAGE = "hexo:page";

    /**
     * 首页列表置顶缓存 key（格式：PageConstant.POST_TOP_PAGE）
     */
    public static final String POST_TOP_PAGE = "hexo:top:page";

    /**
     * 首页归档缓存 key（格式：PageConstant.POST_ARCHIVE + #pageNum）
     */
    public static final String POST_ARCHIVE = "hexo:archive";

    public static final String POST_ARCHIVE_ALL = "hexo:archive:all";

    /**
     * 文章详情页上一页缓存 key（格式：PageConstant.POST_DETAIL_PREVIOUS + #id）
     */
    public static final String POST_DETAIL_PREVIOUS = "hexo:post:detail:previous";

    /**
     * 文章详情页下一页缓存 key（格式：PageConstant.POST_DETAIL_NEXT + #id）
     */
    public static final String POST_DETAIL_NEXT = "hexo:post:detail:next";

    /**
     * 分类名称相关的文章列表缓存 key（格式：PageConstant.POST_BY_CATEGORY_NAME + #categoryName + #pageNum）
     */
    public static final String POST_BY_CATEGORY_NAME = "hexo:post:by:category:name";

    /**
     * 首页分类列表缓存 key（格式：PageConstant.CATEGORY_LIST）
     */
    public static final String CATEGORY_LIST = "hexo:category:list";

    /**
     * 首页友链列表缓存 key（格式：PageConstant.FRIEND_LINK_LIST）
     */
    public static final String FRIEND_LINK_LIST = "hexo:friend:link:list";

    /**
     * 导航页面
     */
    public static final String NAV_PAGE = "nav:page";

    /**
     * 文章评论列表缓存 key（格式：PageConstant.POST_COMMENT + #postId + #pageNum）
     */
    public static final String POST_COMMENT = "hexo:comment";

    /**
     * 留言板缓存 key（格式：PageConstant.GUEST_BOOK + #pageNum）
     */
    public static final String GUEST_BOOK = "hexo:guestBook";
}
