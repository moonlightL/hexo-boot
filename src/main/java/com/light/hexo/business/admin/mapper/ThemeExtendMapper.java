package com.light.hexo.business.admin.mapper;

import com.light.hexo.business.admin.model.ThemeExtend;
import com.light.hexo.common.base.BaseMapper;
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
    void updateByConfigName(@Param("list") List<ThemeExtend> list);
}
