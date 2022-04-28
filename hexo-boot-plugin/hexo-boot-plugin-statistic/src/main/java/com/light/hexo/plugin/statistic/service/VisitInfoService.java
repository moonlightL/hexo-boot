package com.light.hexo.plugin.statistic.service;


import com.light.hexo.plugin.statistic.model.VisitInfo;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: VisitInfoService
 * @ProjectName hexo-boot
 * @Description: 访问记录信息 Service
 * @DateTime 2022/4/24, 0024 16:59
 */
public interface VisitInfoService {

    /**
     * 当天的访问记录
     * @return
     */
    VisitInfo getVisitInfoByToday();

    /**
     * 总的访问记录
     * @return
     */
    VisitInfo getVisitInfoTotal();

    /**
     * 获取指定范围内的访问记录
     * @param startDate
     * @param endDate
     * @return
     */
    List<VisitInfo> listVisitInfoByTimes(LocalDate startDate, LocalDate endDate);
}
