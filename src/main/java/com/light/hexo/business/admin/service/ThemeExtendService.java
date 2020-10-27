package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.ThemeExtend;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;

import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: ThemeExtendService
 * @ProjectName hexo-boot
 * @Description: 主题配置扩展 Service
 * @DateTime 2020/10/27 17:03
 */
public interface ThemeExtendService extends BaseService<ThemeExtend> {

    /**
     * 保存主题配置
     * @param themeId
     * @param extensionMap
     * @throws GlobalException
     */
    void saveThemeExtend(Integer themeId, Map<String, Object> extensionMap) throws GlobalException;
}
