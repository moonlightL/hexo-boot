package com.light.hexo.core.admin.web.controller;

import com.light.hexo.common.util.IpUtil;
import com.light.hexo.common.vo.BlogMetaData;
import com.light.hexo.common.vo.Result;
import com.light.hexo.common.config.BlogConfig;
import com.light.hexo.mapper.model.ActionLog;
import com.light.hexo.mapper.model.Post;
import com.light.hexo.core.admin.service.*;
import com.light.hexo.common.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: HomeController
 * @ProjectName hexo-boot
 * @Description: 主页控制器
 * @DateTime 2020/9/11 15:58
 */
@RequestMapping("/admin/home")
@Controller
public class HomeController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private BlogConfig blogConfig;

    /**
     * 主页
     * @param resultMap
     * @return
     */
    @RequestMapping("/index.html")
    public String index(Map<String, Object> resultMap) {
        int postNum = this.postService.getPostNum();
        int categoryNum = this.categoryService.getCategoryNum();
        int postCommentNum = this.commentService.getCommentNum(1);
        int guestBookNum = this.commentService.getCommentNum(2);

        resultMap.put("postNum", postNum);
        resultMap.put("postCommentNum", postCommentNum);
        resultMap.put("categoryNum", categoryNum);
        resultMap.put("guestBookNum", guestBookNum);
        return super.render("index", resultMap);
    }

    /**
     * 获取访问人数
     * @return
     */
    @RequestMapping("/visitData.json")
    @ResponseBody
    public Result visitData() {

        Map<String, Object> map = new HashMap<>(2);
        int day = 7;
        List<LocalDate> dates = new ArrayList<>(day);
        LocalDate now = LocalDate.now();
        while (day > 0) {
            dates.add(now.minusDays(day));
            day--;
        }

        List<Map<String, Object>> list = this.visitService.listVisitByDates(dates.get(0), dates.get(dates.size() - 1));
        List<String> numList;
        // 近7天都有数据
        if (list.size() == 7) {
            numList = list.stream().map(i -> i.get("num").toString()).collect(Collectors.toList());

        } else {
            // 查询没有记录的日期并填充
            List<String> dateList = list.stream().map(i -> i.get("date").toString()).collect(Collectors.toList());
            for (LocalDate date : dates) {
                if (!dateList.contains(date.toString())) {
                    Map<String, Object> tmp = new HashMap<>(2);
                    tmp.put("date", date.toString());
                    tmp.put("num", 0);
                    list.add(tmp);
                }
            }

            // 日期排序并筛选人数
            numList = list.stream().sorted(Comparator.comparing(o -> o.get("date").toString())).map(i -> i.get("num").toString()).collect(Collectors.toList());
        }

        List<String> dateList = dates.stream().map(LocalDate::toString).collect(Collectors.toList());
        map.put("dates", dateList);
        map.put("values", numList);

        return Result.success(map);
    }

    /**
     * 今日访问数
     * @return
     */
    @RequestMapping("/todayVisitNum.json")
    @ResponseBody
    public Result getTodayVisitNum() {
        Integer visitNum = this.visitService.getTodayVisitNum();
        return Result.success(visitNum);
    }

    /**
     * 获取 top5 文章列表
     * @return
     */
    @RequestMapping("/top5PostList.json")
    @ResponseBody
    public Result top5PostList() {
        List<Post> list = this.postService.listTop5ByReadNum();
        return Result.success(list);
    }

    /**
     * 获取博客元数据
     * @return
     */
    @RequestMapping("/getMetaData.json")
    @ResponseBody
    public Result getMetaData() {
        BlogMetaData metaData = new BlogMetaData();
        metaData.setHomeDir(this.blogConfig.getHomeDir());
        metaData.setAttachmentDir(this.blogConfig.getAttachmentDir());
        metaData.setLogDir(this.blogConfig.getLogDir());
        metaData.setPluginDir(this.blogConfig.getPluginDir());
        metaData.setVersion(this.blogConfig.getVersion());
        return Result.success(metaData);
    }

    /**
     * 获取上次登录信息
     * @return
     */
    @RequestMapping("/lastLoginInfo.json")
    @ResponseBody
    public Result lastLoginInfo() {
        ActionLog actionLog = this.actionLogService.getLastLoginInfo();
        Map<String, Object> map = new HashMap<>();
        map.put("first", actionLog == null);
        if (actionLog != null) {
            map.put("ipInfo", IpUtil.getInfo(actionLog.getIpAddress()));
            map.put("lastLoginTime", actionLog.getCreateTime());
        }
        map.put("currentTime", LocalDateTime.now());
        return Result.success(map);
    }
}
