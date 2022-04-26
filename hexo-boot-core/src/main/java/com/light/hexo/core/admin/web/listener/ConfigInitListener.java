package com.light.hexo.core.admin.web.listener;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.mapper.model.*;
import com.light.hexo.core.admin.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import javax.servlet.ServletContext;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: ConfigInitListener
 * @ProjectName hexo-boot
 * @Description: 初始化和加载全局变量(bean加载完成后执行)
 * @DateTime 2020/10/1 10:57
 */
@Component
@Slf4j
public class ConfigInitListener implements ApplicationListener<ContextRefreshedEvent> {

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

    @Autowired
    private SysPluginService pluginService;

    @Autowired
    public HexoBootPluginManager pluginManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        WebApplicationContext webApplicationContext = (WebApplicationContext) contextRefreshedEvent.getApplicationContext();
        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext == null) {
            log.info("==========ConfigInitListener 获取 ServletContext 为空===========");
            return;
        }

        List<Config> configList = this.configService.findAll();
        Map<String, String> configMap = configList.stream().collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue,  (v1, v2) -> v2));
        servletContext.setAttribute("configMap", configMap);
        log.info("==========ConfigInitListener 加载[全局参数]===========");

        this.loadBlogInfo(servletContext);
        log.info("==========ConfigInitListener 加载[博客信息（导航、分类、标签、友链）]===========");

        this.loadPlugins();
        log.info("==========ConfigInitListener 加载[插件信息]===========");
    }

    private void loadBlogInfo(ServletContext servletContext) {
        // 导航
        this.navService.loadNav(servletContext);

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

    private void loadPlugins() {

        List<SysPlugin> pluginList = this.pluginService.findAll();
        if (CollectionUtils.isEmpty(pluginList)) {
            return;
        }

        for (SysPlugin plugin : pluginList) {
            try {
                String filePath = plugin.getFilePath();
                File pluginFile = new File(filePath);
                if (!pluginFile.exists()) {
                    this.pluginService.removeModel(plugin.getId());
                    continue;
                }

                if (plugin.getState()) {
                    this.pluginManager.loadPlugin(Paths.get(plugin.getFilePath()));
                    this.pluginManager.startPlugin(plugin.getPluginId(), plugin.getFilePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}