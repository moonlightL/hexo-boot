package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.BasePlugin;
import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.plugin.HexoBootSpringExtensionFactory;
import com.light.hexo.common.plugin.ModuleRegistry;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: AbstractModuleRegistry
 * @ProjectName hexo-boot
 * @Description:
 * @DateTime 2022/4/19, 0019 20:18
 */
@Slf4j
public abstract class AbstractModuleRegistry implements ModuleRegistry {

    protected final HexoBootPluginManager pluginManager;

    protected final AbstractAutowireCapableBeanFactory beanFactory;

    public AbstractModuleRegistry(HexoBootPluginManager pluginManager) {
        this.pluginManager = pluginManager;
        this.beanFactory = (AbstractAutowireCapableBeanFactory) this.pluginManager.getApplicationContext().getAutowireCapableBeanFactory();
    }

    protected List<Class<?>> getPluginClasses(String pluginId) throws Exception {
        PluginWrapper pluginWrapper = this.pluginManager.getPlugin(pluginId);
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver(pluginWrapper.getPluginClassLoader());
        String pluginBasePath = ClassUtils.classPackageAsResourcePath(pluginWrapper.getPlugin().getClass());
        //扫描 继承 Plugin 类以及所在子包下的文件
        Resource[] resources = pathMatchingResourcePatternResolver.getResources(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + pluginBasePath + "/**/*.class");
        List<Class<?>> classList = new ArrayList<>();
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = new CachingMetadataReaderFactory().getMetadataReader(resource);
                Class clazz = pluginWrapper.getPluginClassLoader().loadClass(metadataReader.getAnnotationMetadata().getClassName());
                if(!BasePlugin.class.isAssignableFrom(clazz)
                    && !Plugin.class.isAssignableFrom(clazz)
                    && clazz.getAnnotation(Extension.class) == null) {
                    classList.add(clazz);
                }
            }
        }
        return classList;
    }

    protected Object registryBean(Class<?> clazz) {
        Object object = null;
        Map<String, ?> extensionBeanMap = this.pluginManager.getApplicationContext().getBeansOfType(clazz);
        if (extensionBeanMap.isEmpty()) {
            object = this.pluginManager.getExtensionFactory().create(clazz);
            this.beanFactory.registerSingleton(clazz.getName(), object);
        }
        return object;
    }

    protected void destroyBean(Class<?> clazz) {
        HexoBootSpringExtensionFactory extensionFactory = (HexoBootSpringExtensionFactory) this.pluginManager.getExtensionFactory();
        extensionFactory.destroy(clazz);
        this.beanFactory.destroySingleton(clazz.getName());
    }

}
