package com.light.hexo.common.plugin;

import org.pf4j.PluginManager;
import org.pf4j.spring.SpringExtensionFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * @Author MoonlightL
 * @ClassName: HexoBootSpringExtensionFactory
 * @ProjectName hexo-boot
 * @Description: 扩展工厂实现
 * @DateTime 2022/4/19, 0019 20:32
 */
public class HexoBootSpringExtensionFactory extends SpringExtensionFactory implements HexoBootExtensionFactory {

    private Map<String, Object> cacheMap;

    public HexoBootSpringExtensionFactory(PluginManager pluginManager) {
        super(pluginManager, true);
        this.cacheMap = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public <T> T create(Class<T> extensionClass) {
        String beanName = extensionClass.getName();

        if (this.cacheMap.containsKey(beanName)) {
            return (T) this.cacheMap.get(beanName);
        }

        T extension = super.create(extensionClass);
        this.cacheMap.put(beanName, extension);

        return extension;
    }

    @Override
    protected <T> T createWithSpring(Class<T> extensionClass, ApplicationContext applicationContext) {
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        Object autowiredExtension = beanFactory.autowire(extensionClass, 3, false);
        beanFactory.autowireBean(autowiredExtension);
        beanFactory.initializeBean(autowiredExtension, extensionClass.getName());
        return (T) autowiredExtension;
    }

    @Override
    public void destroy(Class<?> extensionClass) {
        String beanName = extensionClass.getName();
        if(this.cacheMap.containsKey(beanName)) {
            this.cacheMap.remove(beanName);
        }
    }
}
