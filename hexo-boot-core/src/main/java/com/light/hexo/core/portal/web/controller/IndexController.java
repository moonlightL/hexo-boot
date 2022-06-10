package com.light.hexo.core.portal.web.controller;

import com.light.hexo.common.vo.Result;
import com.light.hexo.common.constant.ConfigEnum;
import com.light.hexo.mapper.model.*;
import com.light.hexo.common.event.NavEvent;
import com.light.hexo.core.portal.common.CommonController;
import com.light.hexo.core.portal.model.HexoPageInfo;
import com.light.hexo.common.util.HttpClientUtil;
import com.light.hexo.common.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @GetMapping(value = {"", "/index.html", "page/{pageNum}/"})
    public String index(@PathVariable(value="pageNum", required = false) Integer pageNum, Map<String, Object> resultMap) {
        Theme activeTheme = this.themeService.getActiveTheme(true);
        String pageSizeStr = activeTheme.getConfigMap().get("pageSize");
        if (StringUtils.isBlank(pageSizeStr) || !StringUtils.isNumeric(pageSizeStr)) {
            pageSizeStr = this.configService.getConfigValue(ConfigEnum.POST_PAGE_SIZE.getName());
        }

        String filterTopStr = activeTheme.getConfigMap().get("filterTop");
        boolean filterTop = !StringUtils.isBlank(filterTopStr) && (filterTopStr.equals("Yes"));
        pageNum = pageNum == null ? 1 : pageNum;
        HexoPageInfo pageInfo = this.postService.pagePostsByIndex(pageNum, Integer.parseInt(pageSizeStr), filterTop);
        resultMap.put("pageInfo", pageInfo);
        if (filterTop) {
            List<Post> topList = this.postService.findTopList();
            resultMap.put("topList", topList.stream().limit(3).collect(Collectors.toList()));
        }

        resultMap.put("currentNav", this.navService.findByLink("/"));
        return render("index", false, resultMap);
    }

    /**
     * 特殊入口处理
     * @param resultMap
     * @return
     */
    @GetMapping(value = {"blogs/", "blogs/page/{pageNum}/"})
    public String blogs(@PathVariable(value="pageNum", required = false) Integer pageNum, Map<String, Object> resultMap) {
        Theme activeTheme = this.themeService.getActiveTheme(true);
        String pageSizeStr = activeTheme.getConfigMap().get("pageSize");
        if (StringUtils.isBlank(pageSizeStr) || !StringUtils.isNumeric(pageSizeStr)) {
            pageSizeStr = this.configService.getConfigValue(ConfigEnum.POST_PAGE_SIZE.getName());
        }
        pageNum = pageNum == null ? 1 : pageNum;
        HexoPageInfo pageInfo = this.postService.pagePostsByIndex(pageNum, Integer.parseInt(pageSizeStr), false);
        resultMap.put("pageInfo", pageInfo);
        resultMap.put("menu", "blogs");
        resultMap.put("currentNav", this.navService.findByLink("/"));
        return render("blogs", false, resultMap);
    }

    /**
     * 关于
     * @param resultMap
     * @return
     */
    @GetMapping(value = "about/")
    public String about(Map<String, Object> resultMap) {
        UserExtend extend = this.userExtendService.getBloggerInfo();
        resultMap.put("about", extend);
        resultMap.put("currentNav", this.navService.findByLink("/about/"));
        return render("about", false, resultMap);
    }

    /**
     * 搜索框
     * @param resultMap
     * @return
     */
    @GetMapping(value = "search/")
    public String search(Map<String, Object> resultMap) {
        List<Post> postList = this.postService.listPostByIdList(null);
        resultMap.put("postList", postList);
        return render("search", false, resultMap);
    }

    /**
     * 自定义页面导航
     * @param link
     * @param resultMap
     * @return
     */
    @RequestMapping("/custom/{link}")
    public String page(@PathVariable String link, Map<String, Object> resultMap) {
        Nav nav = this.navService.findCustomLink(link);
        resultMap.put("currentNav", nav);
        this.eventPublisher.emit(new NavEvent(this, nav.getId(), NavEvent.Type.READ));
        return render("custom", true, resultMap);
    }

    /**
     * 文章列表
     * @return
     */
    @GetMapping("postList.json")
    @ResponseBody
    public Result getPostList() {
        List<Post> list = this.postService.listPostByIdList(null);
        return Result.success(list);
    }

    /**
     * 音乐列表
     * @return
     */
    @GetMapping("musicList.json")
    @ResponseBody
    public Result musicList() {
        List<Music> list = this.musicService.listMusicByIndex();
        return Result.success(list);
    }

    /**
     * 鸡汤
     * @return
     */
    @GetMapping("chickenSoup.json")
    @ResponseBody
    public Result chickenSoup() {
        String result = HttpClientUtil.sendGet("http://api.lkblog.net/ws/api.php");
        return Result.success(JsonUtil.string2Obj(result, Map.class));
    }
}
