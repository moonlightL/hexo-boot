package com.light.hexo.plugin.statistic.mapper;

import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.plugin.statistic.model.VisitDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: VisitDetailMapper
 * @ProjectName hexo-boot
 * @Description: 访问记录详情 Mapper
 * @DateTime 2022/4/25, 0025 23:12
 */
public interface VisitDetailMapper extends BaseMapper<VisitDetail> {

    /**
     * 通过 url 统计
     * @param pageSize
     * @return
     */
    List<Map<String, Object>> countByUrl(@Param("pageSize") int pageSize);

    /**
     * 通过城市统计
     * @param pageSize
     * @return
     */
    List<Map<String, Object>> countByCity(@Param("pageSize") int pageSize);
}
