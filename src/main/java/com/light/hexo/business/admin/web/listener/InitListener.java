package com.light.hexo.business.admin.web.listener;

import com.light.hexo.business.admin.model.*;
import com.light.hexo.business.admin.service.*;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: InitListener
 * @ProjectName hexo-boot
 * @Description: 初始化和加载全局变量(bean加载完成后执行)
 * @DateTime 2020/10/1 10:57
 */
@Component
@Slf4j
public class InitListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ConfigService configService;

    @Autowired
    private NavService navService;

    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected FriendLinkService friendLinkService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        WebApplicationContext webApplicationContext = (WebApplicationContext) contextRefreshedEvent.getApplicationContext();
        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext == null) {
            log.info("==========InitListener 获取 ServletContext 为空===========");
            return;
        }

        List<Config> configList = this.configService.findAll();
        Map<String, String> configMap = configList.stream().collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue,  (v1, v2) -> v2));
        servletContext.setAttribute("configMap", configMap);
        log.info("==========InitListener 初始化[全局参数]===========");

        this.navService.initNav(servletContext);
        log.info("==========InitListener 初始化[导航数据]===========");

        this.initBlogInfo(servletContext);
        log.info("==========InitListener 初始化[博客信息]===========");

        try {
            FFmpegFrameGrabber.tryLoad();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    private void initBlogInfo(ServletContext servletContext) {
        // 分类
        List<Category> categoryList = this.categoryService.listCategoriesByIndex();
        servletContext.setAttribute("categoryList", categoryList);

        // 标签
        List<Tag> tagList = this.tagService.listTagsByIndex();
        servletContext.setAttribute("tagList", tagList);

        // 友链
        List<FriendLink> friendLinkList = this.friendLinkService.listFriendLinkByIndex();
        servletContext.setAttribute("friendLinkList", friendLinkList);

        // 数量（文章数，标签数，分类数，友链数）
        servletContext.setAttribute("postNum", this.postService.getPostNum());
        servletContext.setAttribute("tagNum", this.tagService.getTagNum());
        servletContext.setAttribute("categoryNum", categoryList.size());
        servletContext.setAttribute("friendLinkNum", friendLinkList.size());

        // 推荐文章（点赞数排名）
        List<Post> recommendPostList = this.postService.listTop5ByPraiseNum();
        servletContext.setAttribute("recommendPostList", recommendPostList);
    }
}