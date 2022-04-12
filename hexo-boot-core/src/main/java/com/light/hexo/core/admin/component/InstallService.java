package com.light.hexo.component;

import com.light.hexo.config.BlogProperty;
import com.light.hexo.constant.HexoExceptionEnum;
import com.light.hexo.mapper.model.*;
import com.light.hexo.core.admin.service.*;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.constant.ConfigEnum;
import com.light.hexo.common.model.InstallRequest;
import com.light.hexo.common.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author Moonlightl
 * @ClassName: InstallService
 * @ProjectName hexo-boot
 * @Description: 安装 Service
 * @DateTime 2020/9/11 17:49
 */
@Component
public class InstallService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserExtendService userExtendService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FriendLinkService friendLinkService;

    @Autowired
    private BlogProperty blogProperty;

    private static final String THEME_DIR = "templates/theme";

    @Transactional(rollbackFor = Exception.class)
    public void installApplication(InstallRequest request, String osName, String browser, String ipAddress) throws Exception {

        // 1. 创建管理员(博主)
        User user = this.createBlogger(request.getUsername(), request.getPassword(), request.getNickname(), request.getEmail());

        // 2. 创建默认分类
        Category category = this.createDefaultCategory();

        // 3. 创建默认文章
        Post post = this.createFirstPost(user, category);

        // 4. 创建留言
        this.createComment(user, osName, browser, ipAddress);

        // 5. 创建友链
        this.createFriendLink();

        // 6. 初始化全局配置
        this.initConfig(user, request.getBlogName(), request.getHomePage(), request.getDescription());
    }

    private User createBlogger(String username, String password, String nickname, String email) {

        int userNum = this.userService.getUserNum();
        if (userNum > 0) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_HAVE_INSTALLED);
        }

        User user = new User();
        user.setUsername(username)
            .setPassword(DigestUtils.md5DigestAsHex(password.getBytes()))
            .setNickname(StringUtils.isNoneBlank(nickname) ? nickname : "博主")
            .setEmail(StringUtils.isNoneBlank(email) ? email : "")
            .setAvatar(HexoConstant.DEFAULT_AVATAR)
            .setState(true)
            .setRole(1);

        this.userService.saveModel(user);

        this.userExtendService.saveUserExtend(user.getId(), "懒人一枚~~");

        return user;
    }

    private Category createDefaultCategory() {

        Category category = new Category();
        category.setName("默认")
                .setCoverUrl(HexoConstant.DEFAULT_CATEGORY_COVER)
                .setState(true)
                .setSort(99)
                .setRemark("默认分类");

        this.categoryService.saveCategory(category);

        return category;
    }

    private Post createFirstPost(User user, Category category) {

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonth().getValue();
        int day = now.getDayOfMonth();

        Post post = new Post();
        post.setTitle("第一篇文章")
            .setSummaryHtml("")
            .setContent("```\n" +
                    "public static void main(String[] args) {\n" +
                    "\tSystem.out.println(\"Hello World\");\n" +
                    "}\n" +
                    "```")
            .setContentHtml(MarkdownUtil.md2html(post.getContent()))
            .setAuthor(user.getNickname())
            .setPublishDate(DateUtil.ldToStr(now))
            .setCoverUrl(HexoConstant.DEFAULT_POST_COVER)
            .setPublish(true)
            .setComment(true)
            .setCategoryId(category.getId())
            .setYear(year + "")
            .setMonth(DateUtil.fillTime(month))
            .setDay(DateUtil.fillTime(day))
            .setLink(year + "/" + month + "/" + day + "/" + StringUtils.replace(post.getTitle(), " ", "-") + "/")
            .setCommentNum(1);

        this.postService.saveModel(post);

        return post;
    }

    private void createComment(User user, String osName, String browserName, String ipAddr) {
        Comment comment = new Comment();
        comment.setPage("/about/")
               .setContent("欢迎大家踊跃留言~~")
               .setNickname(user.getNickname())
               .setAvatar(user.getAvatar())
               .setEmail(user.getEmail())
               .setHomePage(this.configService.getConfigValue(ConfigEnum.HOME_PAGE.getName()))
               .setBlogger(true)
               .setCommentType(2)
               .setOsName(osName)
               .setBrowser(browserName)
               .setIpAddress(ipAddr);
        this.commentService.saveModel(comment);
    }

    private void createFriendLink() {

        FriendLink friendLink = new FriendLink();
        friendLink.setTitle("月光中的污点")
                  .setAuthor("MoonlightL")
                  .setHomeUrl("https://www.extlight.com")
                  .setBackgroundColor("#34495E")
                  .setSort(1)
                  .setRemark("技术博客");
        this.friendLinkService.saveFriendLink(friendLink);
    }

    private void initConfig(User user, String blogName, String homePage, String description) {

        ConfigEnum[] values = ConfigEnum.values();
        List<Config> configList = new ArrayList<>(values.length);
        for (ConfigEnum configEnum : values) {
            Config config = new Config();
            config.setConfigKey(configEnum.getName())
                    .setConfigValue(configEnum.getValue())
                    .setRemark(configEnum.getRemark())
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(config.getCreateTime());

            if (config.getConfigKey().equals(ConfigEnum.IS_INSTALLED.getName())) {
                config.setConfigValue("1");
            }

            if (config.getConfigKey().equals(ConfigEnum.INSTALL_TIME.getName())) {
                config.setConfigValue(DateUtil.ldtToStr(LocalDateTime.now()));
            }

            if (config.getConfigKey().equals(ConfigEnum.BLOG_NAME.getName())) {
                config.setConfigValue(blogName);
            }

            if (config.getConfigKey().equals(ConfigEnum.HOME_PAGE.getName())) {
                config.setConfigValue(homePage);
            }

            if (config.getConfigKey().equals(ConfigEnum.DESCRIPTION.getName())) {
                config.setConfigValue(description);
            }

            if (config.getConfigKey().equals(ConfigEnum.EMAIL.getName())) {
                config.setConfigValue(user.getEmail());
            }

            if (config.getConfigKey().equals(ConfigEnum.BLOG_AUTHOR.getName())) {
                config.setConfigValue(user.getNickname());
            }

            if (config.getConfigKey().equals(ConfigEnum.BACKUP_DIR.getName())) {
                config.setConfigValue(this.blogProperty.getAttachmentDir());
            }

            if (config.getConfigKey().equals(ConfigEnum.LOCAL_FILE_PATH.getName())) {
                // 与 SpringMvcConfig 类中配置的 addResourceHandlers 保持一致
                config.setConfigValue(this.blogProperty.getAttachmentDir());
            }

            configList.add(config);
        }

        this.configService.saveConfigBatch(configList);
    }

}
