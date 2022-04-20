package com.light.hexo.common.plugin;

import org.pf4j.PluginManager;
import org.pf4j.spring.SpringExtensionFactory;

import java.util.*;

/**
 * @Author MoonlightL
 * @ClassName: HexoBootSpringExtensionFactory
 * @ProjectName hexo-boot
 * @Description: 扩展工厂实现
 * @DateTime 2022/4/19, 0019 20:32
 */
public class HexoBootSpringExtensionFactory extends SpringExtensionFactory implements HexoBootExtensionFactory {

//    private Map<String, Object> cacheMap;

    public HexoBootSpringExtensionFactory(PluginManager pluginManager) {
        super(pluginManager, true);
//        this.cacheMap = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> extensionClass) {
        return this.create(extensionClass.getName(), extensionClass);
    }

    @Override
    public <T> T create(String beanName, Class<T> extensionClass) {
//        if (this.cacheMap.containsKey(beanName)) {
//            return (T) this.cacheMap.get(beanName);
//        }

        T extension = super.create(extensionClass);
//        this.cacheMap.put(beanName, extension);

        return extension;
    }

    @Override
    public void destroy(Class<?> extensionClass) {
        String extensionClassName = extensionClass.getName();
//        if(this.cacheMap.containsKey(extensionClassName)) {
//            this.cacheMap.remove(extensionClassName);
//        }
    }
}
