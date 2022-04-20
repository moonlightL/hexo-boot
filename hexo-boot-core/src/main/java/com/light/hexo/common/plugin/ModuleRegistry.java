package com.light.hexo.common.plugin;

/**
 * @Author MoonlightL
 * @ClassName: ModuleRegistry
 * @ProjectName hexo-boot
 * @Description: 模块注册器
 * @DateTime 2022/4/19, 0019 20:14
 */
public interface ModuleRegistry {

    /**
     * 注册模块
     * @param pluginId
     */
    void register(String pluginId) throws Exception;

    /**
     * 卸载模块
     * @param pluginId
     */
    void unRegister(String pluginId) throws Exception;
}
