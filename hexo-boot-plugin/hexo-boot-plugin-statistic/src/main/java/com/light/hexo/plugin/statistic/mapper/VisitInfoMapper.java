package com.light.hexo.plugin.statistic.mapper;

import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.plugin.statistic.model.VisitInfo;

/**
 * @Author MoonlightL
 * @ClassName: VisitInfoMapper
 * @ProjectName hexo-boot
 * @Description: 访问记录信息 Mapper
 * @DateTime 2022/4/24, 0024 16:45
 */
public interface VisitInfoMapper extends BaseMapper<VisitInfo> {

    /**
     * 汇总所有访问记录
     * @return
     */
    VisitInfo sumVisitInfo();
}
