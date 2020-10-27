package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;

import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: ThemeService
 * @ProjectName hexo-boot
 * @Description: 主题 Service
 * @DateTime 2020/9/24 14:37
 */
public interface ThemeService extends BaseService<Theme> {

    /**
     * 获取当前主题
     * @return
     * @throws GlobalException
     */
    Theme getCurrentTheme() throws GlobalException;

    /**
     * 启用主题
     * @param theme
     * @return
     * @throws GlobalException
     */
    void useTheme(Theme theme) throws GlobalException;

    /**
     * 检查主题
     * @param fileDir
     * @return
     * @throws GlobalException
     */
    Theme checkTheme(String fileDir) throws GlobalException;

    /**
     * 保存主题
     * @param themeName
     * @param fileDir
     * @param coverUrl
     * @param state
     * @param remark
     * @param extensionMap
     * @throws GlobalException
     */
    void saveTheme(String themeName, String fileDir, String coverUrl, boolean state, String remark, Map<String, Object> extensionMap) throws GlobalException;
}
