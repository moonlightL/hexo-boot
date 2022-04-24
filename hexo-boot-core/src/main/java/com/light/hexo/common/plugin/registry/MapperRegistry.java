package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.mapper.base.BaseMapper;
import lombok.SneakyThrows;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: MapperRegistry
 * @ProjectName hexo-boot
 * @Description: Mapper 注册器
 * @DateTime 2022/4/20, 0020 9:44
 */
public class MapperRegistry extends AbstractModuleRegistry {

    public MapperRegistry(HexoBootPluginManager pluginManager) {
        super(pluginManager);
    }

    @SneakyThrows
    @Override
    public void register(String pluginId) {
        //注册mapper
        for (Class<?> mapperClass : this.getMapperClassList(pluginId)) {
            GenericBeanDefinition definition = new GenericBeanDefinition();
            definition.getConstructorArgumentValues().addGenericArgumentValue(mapperClass);
            definition.setBeanClass(MapperFactoryBean.class);
            definition.getPropertyValues().add("addToConfig", true);
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            ((GenericWebApplicationContext) super.pluginManager.getApplicationContext()).registerBeanDefinition(mapperClass.getName(), definition);
        }

        PluginWrapper pluginWrapper = super.pluginManager.getPlugin(pluginId);
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver(pluginWrapper.getPluginClassLoader());
        String pluginBasePath = ClassUtils.classPackageAsResourcePath(pluginWrapper.getPlugin().getClass());
        //扫描 plugin 所有的mapper.xml文件
        Resource[] mapperXmlResources = pathMatchingResourcePatternResolver.getResources(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + pluginBasePath + "/*Mapper.xml");

        //注册 mapper.xml
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) super.beanFactory.getBean("sqlSessionFactory");
        Configuration configuration = sqlSessionFactory.getConfiguration();

        try {
            Resources.setDefaultClassLoader(pluginWrapper.getPluginClassLoader());
            for (Resource mapperXmlResource : Arrays.asList(mapperXmlResources)) {
                if(mapperXmlResource != null && mapperXmlResource.getFilename().endsWith("Mapper.xml")) {
                    try {
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperXmlResource.getInputStream(), configuration, mapperXmlResource.toString(), configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                    } catch (Exception e) {
                        throw new NestedIOException("Failed to parse mapping resource: '" + mapperXmlResource + "'", e);
                    } finally {
                        ErrorContext.instance().reset();
                    }
                }
            }
        } finally {
            Resources.setDefaultClassLoader(ClassUtils.getDefaultClassLoader());
        }
    }

    @SneakyThrows
    @Override
    public void unRegister(String pluginId) {
        for (Class<?> mapperClass : this.getMapperClassList(pluginId)) {
            ((GenericWebApplicationContext) super.pluginManager.getApplicationContext()).removeBeanDefinition(mapperClass.getName());
            destroyBean(mapperClass);
        }
    }

    private List<Class<?>> getMapperClassList(String pluginId) throws Exception {
        return super.getPluginClasses(pluginId).stream().filter(clazz -> BaseMapper.class.isAssignableFrom(clazz)).collect(Collectors.toList());
    }
}
