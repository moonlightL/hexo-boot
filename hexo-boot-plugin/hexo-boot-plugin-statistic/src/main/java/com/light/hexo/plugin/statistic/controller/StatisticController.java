package com.light.hexo.plugin.statistic.controller;

import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.vo.Result;
import com.light.hexo.plugin.statistic.model.VisitInfo;
import com.light.hexo.plugin.statistic.service.VisitDetailService;
import com.light.hexo.plugin.statistic.service.VisitInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: StatisticController
 * @ProjectName hexo-boot
 * @Description: 站点控制器
 * @DateTime 2022/4/24, 0024 17:00
 */
@RequestMapping("/plugin/statistic")
@Controller
public class StatisticController extends BaseController {

    @Autowired
    private VisitInfoService visitInfoService;

    @Autowired
    private VisitDetailService visitDetailService;

    /**
     * 获取站点访问信息
     * @return
     */
    @RequestMapping("getSitePageInfo.json")
    @ResponseBody
    public Result getSitePageInfo() {

        Map<String, Object> result = new HashMap<>();
        // 当天的 pv, uv
        VisitInfo todayVisitInfo = this.visitInfoService.getVisitInfoByToday();
        result.put("todayPV", todayVisitInfo == null ? 0 : todayVisitInfo.getPv());
        result.put("todayUV", todayVisitInfo == null ? 0 : todayVisitInfo.getUv());
        // 总的 pv, uv
        VisitInfo totalVisitInfo = this.visitInfoService.getVisitInfoTotal();
        result.put("totalPV", totalVisitInfo == null ? 0 : totalVisitInfo.getPv());
        result.put("totalUV", totalVisitInfo == null ? 0 : totalVisitInfo.getUv());

        return Result.success(result);
    }

    /**
     * 近七天访问数据-用户数统计图
     * @return
     */
    @RequestMapping("getLatestVisitInfo.json")
    @ResponseBody
    public Result getLatestVisitCount() {


        return Result.success();
    }

    /**
     * 近七天访问数据-城市分类统计图
     * @return
     */
    @RequestMapping("getLatestVisitByCity.json")
    @ResponseBody
    public Result getLatestVisitByCity() {

        return Result.success();
    }

    /**
     * 配置页面
     * @param map
     * @return
     */
    @RequestMapping("configUI.html")
    public String configUI(Map<String, Object> map) {
        return this.render("configUI", map);
    }
}
