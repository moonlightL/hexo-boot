package com.light.hexo.common.plugin;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @Author MoonlightL
 * @ClassName: BasePlugin
 * @ProjectName hexo-boot
 * @Description: 插件生命周期管理基类
 * @DateTime 2022/4/19, 0019 20:41
 */
public abstract class BasePlugin extends Plugin {

    private ApplicationContext applicationContext;

    public BasePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        getApplicationContext();
    }

    @Override
    public void stop() {
        if ((this.applicationContext != null) && (this.applicationContext instanceof ConfigurableApplicationContext)) {
            ((ConfigurableApplicationContext) this.applicationContext).close();
        }
    }

    public final ApplicationContext getApplicationContext() {
        if (this.applicationContext == null) {
            this.applicationContext = this.createApplicationContext();
            if(this.applicationContext != null) {
//                ((AnnotationConfigServletWebServerApplicationContext) this.applicationContext).refresh();
                PluginUtil.put(super.wrapper.getPluginId(), this.applicationContext);
            }
        }
        return applicationContext;
    }

    protected ApplicationContext createApplicationContext() {
        AbstractPluginManager pluginManager = (AbstractPluginManager) super.getWrapper().getPluginManager();
        ApplicationContext applicationContext = pluginManager.getApplicationContext();
        return applicationContext;
    }
}
