package com.light.hexo.business.portal.common;

import com.light.hexo.business.admin.model.Category;
import com.light.hexo.business.admin.model.FriendLink;
import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.service.*;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.util.CacheUtil;
import com.light.hexo.common.util.MarkdownUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    protected PostService postService;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected PostCommentService postCommentService;

    @Autowired
    protected TagService tagService;

    @Autowired
    protected GuestBookService guestBookService;

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
    protected EventPublisher eventPublisher;

    protected String render(String pageName, boolean isDetail, Map<String, Object> resultMap) {

        // 主题
        Theme activeTheme = this.themeService.getActiveTheme(true);
        String themeName = (activeTheme == null ? "default" : activeTheme.getName());

        resultMap.put("isDetail", isDetail);
        resultMap.put("prefix", "/theme/" + themeName);
        resultMap.put("activeTheme", activeTheme);
        resultMap.put("md", MarkdownUtil.class);

        this.settingBasePath(activeTheme, resultMap);

        return "theme/" +  themeName + "/" + pageName;
    }

    private void settingBasePath(Theme activeTheme, Map<String, Object> resultMap) {
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
                resultMap.put("baseLink", StringUtils.isBlank(CDNAddress) ? "https://cdn.jsdelivr.net/gh/moonlightL/CDN@" + version + "/" + themeName : CDNAddress);
            } else {
                resultMap.put("baseLink", "/theme/" + themeName);
            }
        } else {
            resultMap.put("baseLink", "/theme/" + themeName);
        }
    }

}
