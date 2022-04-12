package com.light.hexo.core.portal.common;

import com.light.hexo.common.util.MarkdownUtil;
import com.light.hexo.config.BlogProperty;
import com.light.hexo.mapper.model.Theme;
import com.light.hexo.core.admin.service.*;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.util.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: CommonController
 * @ProjectName hexo-boot
 * @Description: 前端控制器基类
 * @DateTime 2020/9/18 17:40
 */
@Slf4j
@Component
public class CommonController {

    protected static final int PAGE_SIZE = 10;

    protected static final String DEFAULT_CDN_ADDRESS = "https://cdn.jsdelivr.net/gh/moonlightL/CDN@%s/%s";

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

    @Autowired
    private BlogProperty blogProperty;

    protected String render(String pageName, boolean isDetail, Map<String, Object> resultMap) {

        // 主题
        Theme activeTheme = this.themeService.getActiveTheme(true);
        String themeName = (activeTheme == null ? "default" : activeTheme.getName());

        resultMap.put("isDetail", isDetail);
        resultMap.put("prefix", "/theme/" + themeName);
        resultMap.put("activeTheme", activeTheme);
        resultMap.put("md", MarkdownUtil.class);

        this.settingBaseLink(activeTheme, resultMap);

        String version = this.blogProperty.getVersion();
        if (StringUtils.isBlank(version) || Double.valueOf(version) > 3.0) {
            // 数量，兼容老版本主题
            Map<String, Integer> countInfo = this.getCountInfo();
            resultMap.put("countInfo", countInfo);
        }

        return String.format("theme/%s/%s", themeName, pageName);
    }

    private void settingBaseLink(Theme activeTheme, Map<String, Object> resultMap) {
        if (activeTheme == null) {
            resultMap.put("baseLink", "/theme/default");
            return;
        }

        String themeName = activeTheme.getName();
        String useCDNStr = activeTheme.getConfigMap().get("useCDN");
        String CDNAddress = activeTheme.getConfigMap().get("CDNAddress");
        String version = activeTheme.getConfigMap().get("version");
        if (StringUtils.isNotBlank(useCDNStr) && StringUtils.isNotBlank(version)) {
            boolean useCDN = useCDNStr.equals("true");
            if (useCDN) {
                resultMap.put("baseLink", StringUtils.isBlank(CDNAddress) ? String.format(DEFAULT_CDN_ADDRESS, version, themeName) : CDNAddress);
            } else {
                resultMap.put("baseLink", "/theme/" + themeName);
            }
        } else {
            resultMap.put("baseLink", "/theme/" + themeName);
        }
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
