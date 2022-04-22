package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.BasePlugin;
import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.plugin.HexoBootSpringExtensionFactory;
import com.light.hexo.common.plugin.ModuleRegistry;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import java.util.*;

/**
 * @Author MoonlightL
 * @ClassName: AbstractModuleRegistry
 * @ProjectName hexo-boot
 * @Description:
 * @DateTime 2022/4/19, 0019 20:18
 */
@Slf4j
public abstract class AbstractModuleRegistry implements ModuleRegistry {

    protected HexoBootPluginManager pluginManager;

    protected DefaultListableBeanFactory beanFactory;

    public AbstractModuleRegistry(HexoBootPluginManager pluginManager) {
        this.pluginManager = pluginManager;
        this.beanFactory = (DefaultListableBeanFactory) this.pluginManager.getApplicationContext().getAutowireCapableBeanFactory();
    }

    protected List<Class<?>> getPluginClasses(String pluginId) throws Exception {
        PluginWrapper pluginWrapper = this.pluginManager.getPlugin(pluginId);
        ClassLoader pluginClassLoader = pluginWrapper.getPluginClassLoader();
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(pluginClassLoader);
        String pluginBasePath = ClassUtils.classPackageAsResourcePath(pluginWrapper.getPlugin().getClass());
        Resource[] resources = resourcePatternResolver.getResources(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + pluginBasePath + "/**/*.class");
        List<Class<?>> classList = new ArrayList<>();
        for (Resource resource : resources) {
            MetadataReader metadataReader = new CachingMetadataReaderFactory().getMetadataReader(resource);
            Class clazz = pluginClassLoader.loadClass(metadataReader.getAnnotationMetadata().getClassName());
            if(!BasePlugin.class.isAssignableFrom(clazz)
                    && !Plugin.class.isAssignableFrom(clazz)
                    && clazz.getAnnotation(Extension.class) == null) {
                classList.add(clazz);
            }
        }
        return classList;
    }

    protected Object registryBean(Class<?> clazz) {
        Object object;
        Map<String, ?> extensionBeanMap = this.pluginManager.getApplicationContext().getBeansOfType(clazz);
        if (extensionBeanMap.isEmpty()) {
            object = this.pluginManager.getExtensionFactory().create(clazz);
            this.beanFactory.registerSingleton(clazz.getName(), object);
            return object;
        }
        return extensionBeanMap.get(clazz.getName());
    }

    protected void destroyBean(Class<?> clazz) {
        this.beanFactory.destroySingleton(clazz.getName());
        HexoBootSpringExtensionFactory extensionFactory = (HexoBootSpringExtensionFactory) this.pluginManager.getExtensionFactory();
        extensionFactory.destroy(clazz);
    }

}
