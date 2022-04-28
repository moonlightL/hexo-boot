package com.light.hexo.plugin.statistic.controller;

import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.util.DateUtil;
import com.light.hexo.common.vo.Result;
import com.light.hexo.plugin.statistic.model.VisitInfo;
import com.light.hexo.plugin.statistic.service.VisitDetailService;
import com.light.hexo.plugin.statistic.service.VisitInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.print.attribute.standard.Sides;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
     * 近七天访问数据
     * @return
     */
    @RequestMapping("getLatestVisitInfo.json")
    @ResponseBody
    public Result getLatestVisitCount() {

        Map<String, Object> result = new HashMap<>();
        int day = 7;
        List<LocalDate> dataList = new ArrayList<>(day);
        LocalDate now = LocalDate.now();
        while (day > 0) {
            dataList.add(now.minusDays(day));
            day--;
        }

        result.put("dateList", dataList);

        List<VisitInfo> visitInfoList = this.visitInfoService.listVisitInfoByTimes(dataList.get(0), dataList.get(dataList.size() - 1));
        if (visitInfoList.size() < 7) {
            List<Integer> existPeriodList = visitInfoList.stream().map(i -> i.getPeriod()).collect(Collectors.toList());
            for (LocalDate date : dataList) {
                Integer formatPeriod = Integer.valueOf(DateUtil.ldToStr(date, DateTimeFormatter.ofPattern("yyyyMMdd")));
                if (!existPeriodList.contains(formatPeriod)) {
                    VisitInfo visitInfo = new VisitInfo();
                    visitInfo.setUv(0).setPv(0).setPeriod(formatPeriod);
                    visitInfoList.add(visitInfo);
                }
            }
            visitInfoList = visitInfoList.stream().sorted(Comparator.comparing(VisitInfo::getPeriod)).collect(Collectors.toList());
        }

        List<Integer> pvList = new ArrayList<>(day);
        List<Integer> uvList = new ArrayList<>(day);

        for (VisitInfo visitInfo : visitInfoList) {
            pvList.add(visitInfo.getPv());
            uvList.add(visitInfo.getUv());
        }

        result.put("pvList", pvList);
        result.put("uvList", uvList);

        return Result.success(result);
    }

    /**
     * 访问页面次数top10
     * @return
     */
    @RequestMapping("getPageDataByTop10.json")
    @ResponseBody
    public Result getPageDataByTop10() {
        List<Map<String, Object>> pageData = this.visitDetailService.getPageData(10);
        Map<String, Object> result = new HashMap<>();
        result.put("pageData", pageData);
        return Result.success(result);
    }

    /**
     * 访问城市来源top10
     * @return
     */
    @RequestMapping("getCityDataByTop10.json")
    @ResponseBody
    public Result getCityDataByTop10() {
        List<Map<String, Object>> cityData = this.visitDetailService.getCityData(10);
        Map<String, Object> result = new HashMap<>();
        result.put("cityData", cityData);
        return Result.success(result);
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
