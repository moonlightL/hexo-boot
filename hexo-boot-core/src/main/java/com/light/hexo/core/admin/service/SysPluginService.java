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
     * 解压插件
     * @param originalFilename
     * @param inputStream
     */
    void unzipPlugin(String originalFilename, InputStream inputStream) throws GlobalException;

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
    void removePlugin(Integer id) throws GlobalException;
}
