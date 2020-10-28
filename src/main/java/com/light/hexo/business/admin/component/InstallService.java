package com.light.hexo.business.admin.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.model.*;
import com.light.hexo.business.admin.service.*;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.common.model.InstallRequest;
import com.light.hexo.common.util.DateUtil;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.JsonUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.ResourceUtils;
import java.io.File;
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
    private PostCommentService postCommentService;

    @Autowired
    private GuestBookService guestBookService;

    @Autowired
    private FriendLinkService friendLinkService;

    @Autowired
    private ThemeService themeService;

    @Transactional(rollbackFor = Exception.class)
    public void installApplication(InstallRequest request, String browserName, String ipAddr) throws Exception {

        // 1. 创建管理员(博主)
        User user = this.createBlogger(request.getUsername(), request.getPassword(), request.getNickname(), request.getEmail());

        // 2. 创建默认分类
        Category category = this.createDefaultCategory();

        // 3. 创建默认文章
        Post post = this.createFirstPost(user, category);

        // 4. 创建评论
        this.createPostComment(post, user, browserName, ipAddr);

        // 5. 创建留言
        this.createGuestBook(user, browserName, ipAddr);

        // 6. 创建友链
        this.createFriendLink();

        // 7. 初始化全局配置
        this.initConfig(user, request.getBlogName(), request.getHomePage(), request.getDescription());

        // 8. 初始化主题数据
        this.initTheme();
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

        this.categoryService.saveModel(category);

        return category;
    }

    private Post createFirstPost(User user, Category category) {

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonth().getValue();
        int day = now.getDayOfMonth();

        Post post = new Post();
        post.setTitle("第一篇文章")
            .setContent("```\n" +
                    "public static void main(String[] args) {\n" +
                    "\tSystem.out.println(\"Hello World\");\n" +
                    "}\n" +
                    "```")
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

    private void createPostComment(Post post, User user, String browserName, String ipAddr) {
        PostComment postComment = new PostComment();
        postComment.setPostId(post.getId())
                   .setTitle(post.getTitle())
                   .setContent("欢迎大家来评论~~")
                   .setUserId(user.getId())
                   .setNickname(user.getNickname())
                   .setBrowser(browserName)
                   .setIpAddress(ipAddr);
        this.postCommentService.saveModel(postComment);
    }

    private void createGuestBook(User user, String browserName, String ipAddr) {
        GuestBook guestBook = new GuestBook();
        guestBook.setContent("欢迎大家踊跃留言~~")
                 .setUserId(user.getId())
                 .setNickname(user.getNickname())
                 .setBrowser(browserName)
                 .setIpAddress(ipAddr);
        this.guestBookService.saveModel(guestBook);
    }

    private void createFriendLink() {

        FriendLink friendLink = new FriendLink();
        friendLink.setTitle("月光中的污点")
                  .setAuthor("MoonlightL")
                  .setHomeUrl("https://www.extlight.com")
                  .setEmail("jx8996@163.com")
                  .setBackgroundColor("#34495E")
                  .setSort(1)
                  .setRemark("技术博客");
        this.friendLinkService.saveModel(friendLink);
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
                config.setConfigValue(System.getProperty("user.home") + File.separator + ".hexo-boot-attachment" + File.separator);
            }

            if (config.getConfigKey().equals(ConfigEnum.LOCAL_FILE_PATH.getName())) {
                // 与 SpringMvcConfig 类中配置的 addResourceHandlers 保持一致
                config.setConfigValue(System.getProperty("user.home") + File.separator + ".hexo-boot-attachment" + File.separator);
            }

            configList.add(config);
        }

        this.configService.saveConfigBatch(configList);
    }

    private void initTheme() throws Exception {

        File dir = ResourceUtils.getFile("classpath:templates/theme");
        File[] files = dir.listFiles((file, name) -> name.equals("default"));
        if (files == null || files.length == 0) {
            ExceptionUtil.throwEx(404, "主题目录不存在");
        }

        File parent = files[0];
        File[] jsonFiles = parent.listFiles((file, name) -> name.equals("theme.json"));
        if (jsonFiles == null) {
            ExceptionUtil.throwEx(404, "主题配置文件不存在");
        }

        File jsonFile = jsonFiles[0];

        // 读取内容
        String content = FileUtils.readFileToString(jsonFile, "UTF-8");
        Map<String, Object> map = JsonUtil.string2Obj(content, new TypeReference<Map<String, Object>>() {});
        String fileDir = jsonFile.getParentFile().getName();
        this.themeService.saveTheme(
                Objects.nonNull(map.get("name")) ? map.get("name").toString(): jsonFile.getParentFile().getName(),
                String.format("/theme/%s/preview.png", fileDir),
                true,
                Objects.nonNull(map.get("remark")) ? map.get("remark").toString(): "",
                (List<Map<String, String>>)map.get("extension")
        );
    }
}
