package com.light.hexo.mapper.mapper;

import com.light.hexo.mapper.model.ThemeExtend;
import com.light.hexo.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: ThemeExtendMapper
 * @ProjectName hexo-boot
 * @Description: 主题配置扩展 Mapper
 * @DateTime 2020/10/27 17:02
 */
public interface ThemeExtendMapper extends BaseMapper<ThemeExtend> {

    /**
     * 插入/修改配置
     * @param list
     */
    void updateBatchByConfigName(@Param("list") List<ThemeExtend> list);

    /**
     * 修改配置
     * @param themeExtendList
     */
    void updateBatchById(@Param("list") List<ThemeExtend> themeExtendList);
}
