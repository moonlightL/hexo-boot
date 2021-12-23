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
     * 文章详情页缓存 key（格式：PageConstant.POST_DETAIL_INFO + #link）
     */
    public static final String POST_DETAIL_INFO = "hexo:post:detail:info";

    /**
     * 分类名称相关的文章列表缓存 key（格式：PageConstant.POST_BY_CATEGORY_NAME + #categoryName + #pageNum）
     */
    public static final String POST_BY_CATEGORY_NAME = "hexo:post:by:category:name";

    /**
     * 标签名称相关的文章列表缓存 key（格式：PageConstant.POST_BY_TAG_NAME + #tagName + #pageNum）
     */
    public static final String POST_BY_TAG_NAME = "hexo:post:by:tag:name";

    /**
     * 首页分类列表缓存 key（格式：PageConstant.CATEGORY_LIST）
     */
    public static final String CATEGORY_LIST = "hexo:category:list";

    /**
     * 首页标签列表缓存 key（格式：PageConstant.TAG_LIST）
     */
    public static final String TAG_LIST = "hexo:tag:list";

    /**
     * 首页友链列表缓存 key（格式：PageConstant.FRIEND_LINK_LIST）
     */
    public static final String FRIEND_LINK_LIST = "hexo:friend:link:list";

    /**
     * 首页专辑列表缓存 key（格式：PageConstant.ALBUM_LIST）
     */
    public static final String ALBUM_LIST = "hexo:album:list";

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

    /**
     * 首页动态缓存 key（格式：PageConstant.DYNAMIC_LIST + #pageNum）
     */
    public static final String DYNAMIC_LIST = "hexo:dynamic:list";
}
