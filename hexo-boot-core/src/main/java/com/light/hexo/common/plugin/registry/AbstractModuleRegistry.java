package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.BasePlugin;
import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.plugin.HexoBootSpringExtensionFactory;
import com.light.hexo.common.plugin.ModuleRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    public static final Map<String, List<Class<?>>> PLUGIN_CLASS_MAP = new ConcurrentHashMap<>();

    public AbstractModuleRegistry(HexoBootPluginManager pluginManager) {
        this.pluginManager = pluginManager;
        this.beanFactory = (DefaultListableBeanFactory) this.pluginManager.getApplicationContext().getAutowireCapableBeanFactory();
    }

    @SneakyThrows
    protected List<Class<?>> getPluginClasses(String pluginId) {
        List<Class<?>> classList = PLUGIN_CLASS_MAP.get(pluginId);
        if (CollectionUtils.isEmpty(classList)) {
            classList = new ArrayList<>();
            PluginWrapper pluginWrapper = this.pluginManager.getPlugin(pluginId);
            ClassLoader pluginClassLoader = pluginWrapper.getPluginClassLoader();

            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(pluginClassLoader);
            String pluginBasePath = ClassUtils.classPackageAsResourcePath(pluginWrapper.getPlugin().getClass());
            Resource[] resources = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + pluginBasePath + "/**/*.class");

            MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getAnnotationMetadata().getClassName();
                Class clazz = pluginClassLoader.loadClass(className);
                if(!BasePlugin.class.isAssignableFrom(clazz) &&
                   !Plugin.class.isAssignableFrom(clazz) &&
                   clazz.getAnnotation(Extension.class) == null) {
                    classList.add(clazz);
                }
            }

            PLUGIN_CLASS_MAP.put(pluginId, classList);
        }

        return classList;
    }

    protected void clearPluginClass(String pluginId) {
        if (PLUGIN_CLASS_MAP.containsKey(pluginId)) {
            PLUGIN_CLASS_MAP.remove(pluginId);
        }
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
        HexoBootSpringExtensionFactory extensionFactory = (HexoBootSpringExtensionFactory) this.pluginManager.getExtensionFactory();
        Object pluginBean = extensionFactory.getPluginBean(clazz);
        if (pluginBean != null) {
            this.beanFactory.destroyBean(pluginBean);
        }
        this.beanFactory.destroySingleton(clazz.getName());
        extensionFactory.destroy(clazz);
    }

}
