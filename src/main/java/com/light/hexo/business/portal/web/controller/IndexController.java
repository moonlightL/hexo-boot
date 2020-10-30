package com.light.hexo.business.portal.web.controller;

import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.model.Post;
import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.model.UserExtend;
import com.light.hexo.business.portal.common.CommonController;
import com.light.hexo.business.portal.model.HexoPageInfo;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: IndexController
 * @ProjectName hexo-boot
 * @Description: 首页控制器
 * @DateTime 2020/9/18 17:44
 */
@Controller
public class IndexController extends CommonController {

    /**
     * 首页
     * @param resultMap
     * @return
     */
    @GetMapping(value = {"/", "/index.html"})
    public String index(HttpServletRequest request, Map<String, Object> resultMap) {
        Theme activeTheme = this.themeService.getActiveTheme();
        String pageSizeStr = activeTheme.getConfigMap().get("pageSize");
        if (StringUtils.isBlank(pageSizeStr) || !StringUtils.isNumeric(pageSizeStr)) {
            pageSizeStr = this.configService.getConfigValue(ConfigEnum.POST_PAGE_SIZE.getName());
        }
        HexoPageInfo pageInfo = this.postService.pagePostsByIndex(1, Integer.parseInt(pageSizeStr));
        resultMap.put("pageInfo", pageInfo);
        resultMap.put("menu", "index");
        return render("index", false, resultMap);
    }

    @GetMapping("/page/{pageNum}/")
    public String indexPage(@PathVariable Integer pageNum, Map<String, Object> resultMap) {
        Theme activeTheme = this.themeService.getActiveTheme();
        String pageSizeStr = activeTheme.getConfigMap().get("pageSize");
        if (StringUtils.isBlank(pageSizeStr) || !StringUtils.isNumeric(pageSizeStr)) {
            pageSizeStr = this.configService.getConfigValue(ConfigEnum.POST_PAGE_SIZE.getName());
        }
        HexoPageInfo pageInfo = this.postService.pagePostsByIndex(pageNum, Integer.parseInt(pageSizeStr));
        resultMap.put("pageInfo", pageInfo);
        resultMap.put("menu", "index");
        return render("index", false, resultMap);
    }

    /**
     * 特殊入口处理
     * @param resultMap
     * @return
     */
    @GetMapping("/blogs/")
    public String blogs(Map<String, Object> resultMap) {
        String pageSizeStr = this.configService.getConfigValue(ConfigEnum.POST_PAGE_SIZE.getName());
        HexoPageInfo pageInfo = this.postService.pagePostsByIndex(1, Integer.parseInt(pageSizeStr));
        resultMap.put("pageInfo", pageInfo);
        resultMap.put("menu", "blogs");
        return render("blogs", false, resultMap);
    }

    /**
     * 特殊入口处理
     * @param resultMap
     * @return
     */
    @GetMapping("/blogs/page/{pageNum}/")
    public String blogs(@PathVariable Integer pageNum, Map<String, Object> resultMap) {
        String pageSizeStr = this.configService.getConfigValue(ConfigEnum.POST_PAGE_SIZE.getName());
        HexoPageInfo pageInfo = this.postService.pagePostsByIndex(pageNum, Integer.parseInt(pageSizeStr));
        resultMap.put("pageInfo", pageInfo);
        resultMap.put("menu", "blogs");
        return render("blogs", false, resultMap);
    }

    /**
     * 文章内容，URL 的配置格式是为了兼容 hexo
     * @param year
     * @param month
     * @param day
     * @param title
     * @param resultMap
     * @return
     */
    @GetMapping("/{year}/{month}/{day}/{title}/")
    public String post(@PathVariable("year") String year,
                       @PathVariable("month") String month,
                       @PathVariable("day") String day,
                       @PathVariable("title") String title,
                       Map<String, Object> resultMap) {
        String link = year + "/" + month + "/" + day + "/" + title + "/";
        Post post = this.postService.getDetailInfo(link);
        Post previousPost = this.postService.getPreviousInfo(post.getId());
        Post nextPost = this.postService.getNextInfo(post.getId());

        resultMap.put("post", post);
        resultMap.put("previousPost", previousPost);
        resultMap.put("nextPost", nextPost);

        return render("detail", true, resultMap);
    }

    /**
     * 关于
     * @param resultMap
     * @return
     */
    @GetMapping(value = "/about/")
    public String about(Map<String, Object> resultMap) {
        UserExtend extend = this.userExtendService.getBloggerInfo();
        resultMap.put("about", extend);
        resultMap.put("menu", "about");
        return render("about", false, resultMap);
    }

    /**
     * 文章点赞
     * @param postId
     * @return
     */
    @PostMapping("/praisePost/{postId}")
    @ResponseBody
    public Result prizePost(@PathVariable Integer postId, HttpServletRequest request) {
        String ipAddr = IpUtil.getIpAddr(request);
        int prizeNum = this.postService.praisePost(ipAddr, postId);
        return Result.success(prizeNum);
    }

    /**
     * 文章列表
     * @return
     */
    @GetMapping("/postList.json")
    @ResponseBody
    public Result getPostList() {
        List<Post> list = this.postService.listPostByIdList(null);
        return Result.success(list);
    }
}
