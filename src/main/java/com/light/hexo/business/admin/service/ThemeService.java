package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.TreeNode;

import java.io.IOException;
import java.util.List;
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
    Theme getActiveTheme() throws GlobalException;

    /**
     * 启用主题
     * @param theme
     * @return
     * @throws GlobalException
     */
    void useTheme(Theme theme) throws GlobalException;

    /**
     * 检查主题
     * @param name
     * @return
     * @throws GlobalException
     */
    Theme checkTheme(String name) throws GlobalException;

    /**
     * 保存主题
     * @param themeName
     * @param coverUrl
     * @param state
     * @param remark
     * @param extension
     * @throws GlobalException
     */
    void saveTheme(String themeName, String coverUrl, boolean state, String remark, List<Map<String, String>> extension) throws GlobalException;

    /**
     * 批量删除
     * @param themeList
     * @throws GlobalException
     */
    void deleteThemeBatch(List<Theme> themeList) throws GlobalException;

    /**
     * 获取主题目录
     * @param theme
     * @return
     * @throws GlobalException
     */
    List<TreeNode> getThemeCatalog(Theme theme) throws GlobalException;

    /**
     * 获取主题文件内容
     * @param path
     * @return
     * @throws GlobalException
     */
    String getThemeFileContent(String path) throws GlobalException, IOException;

    /**
     * 编辑主题文件内容
     * @param path
     * @param content
     * @throws GlobalException
     */
    void editThemeFileContent(String path, String content) throws GlobalException, IOException;

    /**
     * 拉取主题
     * @param themeUrl
     * @throws GlobalException
     * @throws IOException
     */
    void fetchTheme(String themeUrl) throws GlobalException;
}
