package com.light.hexo.plugin.statistic.service;

import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: VisitDetailService
 * @ProjectName hexo-boot
 * @Description: 访问记录详情 Service
 * @DateTime 2022/4/25, 0025 11:12
 */
public interface VisitDetailService {

    /**
     * 检查对应 ip 的记录数
     * @param ip
     * @param startTime
     * @param endTime
     * @return
     */
    int checkVisitDetail(String ip, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 保存访问记录详情
     * @param ip
     * @param url
     * @param browser
     */
    void saveVisitDetail(String ip, String url, String browser);
}
