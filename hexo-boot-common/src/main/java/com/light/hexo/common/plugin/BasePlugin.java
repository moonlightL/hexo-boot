package com.light.hexo.common.plugin;

import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.springframework.context.ApplicationContext;

/**
 * @Author MoonlightL
 * @ClassName: BasePlugin
 * @ProjectName hexo-boot
 * @Description: 插件生命周期管理基类
 * @DateTime 2022/4/19, 0019 20:41
 */
public abstract class BasePlugin extends SpringPlugin {

    protected ApplicationContext applicationContext;

    public BasePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        this.applicationContext = this.createApplicationContext();
        PluginUtil.put(super.wrapper.getPluginId(), this.applicationContext);
    }

    @Override
    public void stop() {
        PluginUtil.remove(super.wrapper.getPluginId());
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AbstractPluginManager pluginManager = (AbstractPluginManager) super.getWrapper().getPluginManager();
        ApplicationContext applicationContext = pluginManager.getApplicationContext();
        return applicationContext;
    }

    public abstract String getConfigUrl();
}
