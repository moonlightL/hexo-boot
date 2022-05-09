package com.light.hexo.core.admin.service;

import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.mapper.model.SysPlugin;

import java.io.InputStream;

/**
 * @Author MoonlightL
 * @ClassName: SysPluginService
 * @ProjectName hexo-boot
 * @Description: 插件 Service
 * @DateTime 2022/4/13, 0013 10:33
 */
public interface SysPluginService extends BaseService<SysPlugin> {

    /**
     * 安装插件
     * @param originalFilename
     * @param inputStream
     * @return 插件 id
     * @throws GlobalException
     */
    String installPlugin(String originalFilename, InputStream inputStream) throws GlobalException;

    /**
     * 修改插件
     * @param plugin
     */
    void updatePlugin(SysPlugin plugin) throws GlobalException;

    /**
     * 卸载插件
     * @param id
     * @throws GlobalException
     */
    void uninstallPlugin(Integer id) throws GlobalException;

    /**
     *  检测指定插件（是否安装、开启状态）
     * @param pluginId
     * @return
     */
    boolean checkPlugin(String pluginId) throws GlobalException;

    /**
     * 清除缓存
     */
    void clearCache() throws GlobalException;
}
