package com.light.hexo.business.portal.web.controller;

import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.model.Post;
import com.light.hexo.business.admin.service.ConfigService;
import com.light.hexo.business.admin.service.PostService;
import com.light.hexo.business.portal.model.SiteMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: SiteMapController
 * @ProjectName hexo-boot
 * @Description: 站点地图控制器
 * @DateTime 2021/1/9 22:19
 */
@RestController
public class SiteMapController {

    private static String CHANGEFREQ_DAILY = "daily";

    @Autowired
    private ConfigService configService;

    @Autowired
    private PostService postService;

    @GetMapping(value = "/sitemap.xml", produces = {"application/xml"})
    public String getSiteMap() {

        String homePage = this.configService.getConfigValue(ConfigEnum.HOME_PAGE.getName());

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        sb.append(new SiteMap(homePage));

        List<Post> postList = this.postService.findAll();
        Collections.reverse(postList);
        for (Post post : postList) {
            sb.append(new SiteMap(homePage + "/" + post.getLink(), post.getUpdateTime().toLocalDate(), CHANGEFREQ_DAILY, "0.9"));
        }

        sb.append("</urlset>");
        return sb.toString();
    }
}
