package com.light.core.test;

import com.light.hexo.HexoBootApplication;
import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.util.SpringContextUtil;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author MoonlightL
 * @ClassName: PluginService
 * @ProjectName hexo-boot
 * @Description: 插件测试
 * @DateTime 2022/4/12, 0012 18:15
 */
@SpringBootTest(classes = HexoBootApplication.class)
public class PluginServiceTest {

    @Test
    public void test() throws Exception {

        PluginManager pluginManager = SpringContextUtil.getBean(HexoBootPluginManager.class);

        String path = "D:\\data\\hexo-boot-plugin-server-1.0.0.jar";
        String pluginId = pluginManager.loadPlugin(Paths.get(path));
        ClassLoader pluginClassLoader = pluginManager.getPluginClassLoader(pluginId);

        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(pluginClassLoader);
        String pluginBasePath = ClassUtils.classPackageAsResourcePath(pluginWrapper.getPlugin().getClass());
        Resource[] resources = resourcePatternResolver.getResources(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + pluginBasePath + "/**/*.class");
        List<Class<?>> classList = new ArrayList<>();
        for (Resource resource : resources) {
            MetadataReader metadataReader = new CachingMetadataReaderFactory().getMetadataReader(resource);
            Class clazz = pluginClassLoader.loadClass(metadataReader.getAnnotationMetadata().getClassName());
            classList.add(clazz);
        }

        Optional<Class<?>> first = classList.stream().filter(i -> i.getName().equals("com.light.hexo.plugin.server.model.ServerInfo")).findFirst();
        if (first.isPresent()) {
            Class<?> clazz = first.get();
            Object o = clazz.newInstance();
            Method method = clazz.getMethod("collectInfo");
            method.invoke(o);
            System.out.println(o);
        }

        URLClassLoader urlClassLoader = (URLClassLoader) pluginClassLoader;
        urlClassLoader.close();

        boolean deleteQuietly = FileUtils.deleteQuietly(new File(path));
        System.out.println(deleteQuietly);

    }

}
