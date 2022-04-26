package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.mapper.base.BaseMapper;
import lombok.SneakyThrows;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.NestedIOException;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.spring.mapper.MapperFactoryBean;
import tk.mybatis.spring.mapper.SpringBootBindUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

        SqlSessionFactory sqlSessionFactory = super.beanFactory.getBean(SqlSessionFactory.class);
        Configuration configuration = sqlSessionFactory.getConfiguration();

        Environment environment = super.beanFactory.getBean(Environment.class);
        Config config = SpringBootBindUtil.bind(environment,Config.class, Config.PREFIX);
        MapperHelper mapperHelper = new MapperHelper();
        mapperHelper.setConfig(config);

        ClassLoader pluginClassLoader = super.pluginManager.getPluginClassLoader(pluginId);
        Thread.currentThread().setContextClassLoader(pluginClassLoader);
        GenericWebApplicationContext applicationContext = (GenericWebApplicationContext) super.pluginManager.getApplicationContext();

        for (Class<?> mapperClass : this.getMapperClassList(pluginId)) {
            GenericBeanDefinition definition = new GenericBeanDefinition();
            definition.getConstructorArgumentValues().addGenericArgumentValue(mapperClass);
            MapperFactoryBean mapperFactoryBean = new MapperFactoryBean();
            mapperFactoryBean.setMapperInterface(mapperClass);
            definition.setBeanClass(mapperFactoryBean.getClass());
            definition.getPropertyValues().add("mapperHelper", mapperHelper);
            definition.getPropertyValues().add("addToConfig", true);
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            applicationContext.registerBeanDefinition(mapperClass.getName(), definition);

            Type[] baseMapperClass = mapperClass.getGenericInterfaces();
            if (baseMapperClass == null || baseMapperClass.length == 0) {
                continue;
            }

            Class<?> entityClass = (Class<?>) ((ParameterizedType) baseMapperClass[0]).getActualTypeArguments()[0];
            EntityHelper.initEntityNameMap(entityClass, mapperHelper.getConfig());
        }

        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver(pluginClassLoader);
        Resource[] mapperXmlResources = pathMatchingResourcePatternResolver.getResources(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "mapper/*Mapper.xml");

        try {
            Resources.setDefaultClassLoader(pluginClassLoader);
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

        GenericWebApplicationContext applicationContext = (GenericWebApplicationContext) super.pluginManager.getApplicationContext();

        Field field = EntityHelper.class.getDeclaredField("entityTableMap");
        field.setAccessible(true);
        Map<Class<?>, EntityTable> entityTableMap = (Map<Class<?>, EntityTable>) field.get(EntityHelper.class);

        for (Class<?> mapperClass : this.getMapperClassList(pluginId)) {
            try {
                applicationContext.removeBeanDefinition(mapperClass.getName());
                super.destroyBean(mapperClass);

                Type[] baseMapperClass = mapperClass.getGenericInterfaces();
                if (baseMapperClass == null || baseMapperClass.length == 0) {
                    continue;
                }

                Class<?> entityClass = (Class<?>) ((ParameterizedType) baseMapperClass[0]).getActualTypeArguments()[0];
                entityTableMap.remove(entityClass);

            } catch (NoSuchBeanDefinitionException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Class<?>> getMapperClassList(String pluginId) throws Exception {
        return super.getPluginClasses(pluginId).stream().filter(clazz -> BaseMapper.class.isAssignableFrom(clazz)).collect(Collectors.toList());
    }

}
