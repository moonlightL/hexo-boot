package com.light.hexo.core.portal.common;

import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.constant.RequestFilterConstant;
import com.light.hexo.common.util.CacheUtil;
import com.light.hexo.common.util.MarkdownUtil;
import com.light.hexo.common.util.RequestUtil;
import com.light.hexo.core.admin.service.*;
import com.light.hexo.mapper.model.Theme;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author MoonlightL
 * @ClassName: CommonController
 * @ProjectName hexo-boot
 * @Description: 前端控制器基类
 * @DateTime 2020/9/18 17:40
 */
@Slf4j
public class CommonController {

    protected static final int PAGE_SIZE = 10;

    @Autowired
    protected PostService postService;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected CommentService commentService;

    @Autowired
    protected TagService tagService;

    @Autowired
    protected UserExtendService userExtendService;

    @Autowired
    protected FriendLinkService friendLinkService;

    @Autowired
    protected ThemeService themeService;

    @Autowired
    protected ConfigService configService;

    @Autowired
    protected NavService navService;

    @Autowired
    protected MusicService musicService;

    @Autowired
    protected DynamicService dynamicService;

    @Autowired
    protected AlbumService albumService;

    @Autowired
    protected AlbumDetailService albumDetailService;

    @Autowired
    protected EventPublisher eventPublisher;

    private static final Map<String, String> NAV_MAP;

    static {
        NAV_MAP = new HashMap<>();
        NAV_MAP.put("index", "/");
        NAV_MAP.put("blogs", "/");
        NAV_MAP.put("about", "/about/");
        NAV_MAP.put("albums", "/albums/");
        NAV_MAP.put("albumDetail", "/albums/");
        NAV_MAP.put("archives", "/archives/");
        NAV_MAP.put("categories", "/categories/");
        NAV_MAP.put("dynamics", "/dynamics/");
        NAV_MAP.put("friendLinks", "/friendLinks/");
        NAV_MAP.put("tags", "/tags/");
    }

    protected String render(String pageName, boolean isDetail, Map<String, Object> resultMap) {

        // 数量（兼容数据）
        Map<String, Integer> countInfo = this.getCountInfo();
        resultMap.put("countInfo", countInfo);

        // 主题
        Theme activeTheme = this.themeService.getActiveTheme(true);
        String themeName = (activeTheme == null ? "default" : activeTheme.getName());

        resultMap.put("isDetail", isDetail);
        resultMap.put("prefix", "/theme/" + themeName);
        resultMap.put("activeTheme", activeTheme);
        resultMap.put("md", MarkdownUtil.class);

        this.settingBaseLink(activeTheme, resultMap);
        this.setCurrentNav(pageName, resultMap);
        this.setVisitCookie();

        return String.format("theme/%s/%s", themeName, pageName);
    }

    private void settingBaseLink(Theme activeTheme, Map<String, Object> resultMap) {
        if (activeTheme == null) {
            resultMap.put("baseLink", "/theme/default");
            return;
        }

        String themeName = activeTheme.getName();
        String useCDNStr = activeTheme.getConfigMap().get("useCDN");
        if (!useCDNStr.equals("true")) {
            resultMap.put("baseLink", "/theme/" + themeName);
            return;
        }

        String CDNAddress = activeTheme.getConfigMap().get("CDNAddress");
        resultMap.put("baseLink", StringUtils.isBlank(CDNAddress) ? "/theme/" + themeName : CDNAddress);
    }

    private void setCurrentNav(String pageName, Map<String, Object> resultMap) {

        // 自定义页面直接返回
        if (resultMap.containsKey("currentNav")) {
            return;
        }

        resultMap.put("currentNav", this.navService.findByLink(NAV_MAP.get(pageName)));
    }

    private void setVisitCookie() {
        HttpServletRequest request = RequestUtil.getHttpServletRequest();
        if (request != null) {
            HttpServletResponse response = RequestUtil.getHttpServletResponse();
            Cookie[] cookies = request.getCookies();
            if (cookies == null || cookies.length == 0) {
                // 客户端手动清除 cookie
                this.writeCookie(request, response);
            } else {
                Optional<Cookie> first = Arrays.stream(cookies).filter(i -> i.getName().equalsIgnoreCase(RequestFilterConstant.VISIT_COOKIE_NAME)).findFirst();
                if (!first.isPresent()) {
                    // 当日首次访问
                    this.writeCookie(request, response);
                }
            }
        }
    }

    private void writeCookie(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String uuid = sessionId + "-" + RandomStringUtils.randomAlphanumeric(15);
        Cookie cookie = new Cookie(RequestFilterConstant.VISIT_COOKIE_NAME, uuid);
        cookie.setPath("/");
        cookie.setMaxAge(90 * 24 * 3600);
        response.addCookie(cookie);
    }

    private Map<String, Integer> getCountInfo() {

        String key = CacheKey.INDEX_COUNT_INFO;
        Map<String, Integer> result = CacheUtil.get(key);
        if (result == null) {
            result = new HashMap<>(4);
            // 文章数
            result.put("postNum", this.postService.getPostNum());
            // 分类数
            result.put("categoryNum", this.categoryService.getCategoryNum());
            // 标签数
            result.put("tagNum", this.tagService.getTagNum());
            // 友链数
            result.put("friendLinkNum", this.friendLinkService.getFriendLinkNum());
            // 缓存一天
            CacheUtil.put(key, result, 24 * 60 * 60 * 1000);
        }

        return result;
    }

}
