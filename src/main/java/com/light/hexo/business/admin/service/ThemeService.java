package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.model.extend.ThemeFileExtension;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.TreeNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
    Theme getActiveTheme(boolean cache) throws GlobalException;

    /**
     * 启用主题
     * @param themeId
     * @return
     * @throws GlobalException
     */
    void useTheme(Integer themeId) throws GlobalException;

    /**
     * 通过名称获取主题
     * @param name
     * @return
     * @throws GlobalException
     */
    Theme getTheme(String name) throws GlobalException;

    /**
     * 保存主题
     * @param themeName
     * @param coverUrl
     * @param state
     * @param remark
     * @param extensionList
     * @throws GlobalException
     */
    Integer saveTheme(String themeName, String coverUrl, boolean state, String remark, List<ThemeFileExtension> extensionList) throws GlobalException;

    /**
     * 批量删除
     * @param idList
     * @throws GlobalException
     */
    void deleteThemeBatch(List<Integer> idList) throws GlobalException;

    /**
     * 获取主题目录
     * @param theme
     * @return
     * @throws GlobalException
     */
    List<TreeNode> getThemeTreeNode(Theme theme) throws GlobalException;

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
    boolean fetchTheme(String themeUrl) throws GlobalException;

    /**
     * 解压主题
     * @param inputStream
     * @throws GlobalException
     */
    String unzipTheme(InputStream inputStream) throws GlobalException;

    /**
     * 获取主题目录
     * @return
     * @throws GlobalException
     */
    File getThemeCatalog(boolean sync) throws GlobalException;

    /**
     * 启动时，检测主题
     * @throws GlobalException
     */
    void checkThemeByStartup() throws GlobalException, IOException;

    /**
     * 删除主题
     * @param id
     * @throws GlobalException
     */
    void removeTheme(Integer id) throws GlobalException;

}
