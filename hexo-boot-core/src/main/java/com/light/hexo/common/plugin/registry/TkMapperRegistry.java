package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.mapper.base.BaseMapper;
import lombok.SneakyThrows;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.NestedIOException;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.util.MsUtil;
import tk.mybatis.spring.mapper.MapperFactoryBean;
import tk.mybatis.spring.mapper.SpringBootBindUtil;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: TkMapperRegistry
 * @ProjectName hexo-boot
 * @Description: Mapper 注册器
 * @DateTime 2022/4/20, 0020 9:44
 */
public class TkMapperRegistry extends AbstractModuleRegistry {

    public TkMapperRegistry(HexoBootPluginManager pluginManager) {
        super(pluginManager);
    }

    @SneakyThrows
    @Override
    public void register(String pluginId) {

        ClassLoader pluginClassLoader = super.pluginManager.getPluginClassLoader(pluginId);
        Thread.currentThread().setContextClassLoader(pluginClassLoader);

        SqlSessionFactory sqlSessionFactory = super.beanFactory.getBean(SqlSessionFactory.class);
        Configuration configuration = sqlSessionFactory.getConfiguration();

        try {

            MapperHelper mapperHelper = new MapperHelper();
            mapperHelper.setConfig(SpringBootBindUtil.bind(super.beanFactory.getBean(Environment.class), Config.class, Config.PREFIX));

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
                if (baseMapperClass != null && baseMapperClass.length > 0) {
                    Class<?> entityClass = (Class<?>) ((ParameterizedType) baseMapperClass[0]).getActualTypeArguments()[0];
                    EntityHelper.initEntityNameMap(entityClass, mapperHelper.getConfig());
                }
            }

            Resources.setDefaultClassLoader(pluginClassLoader);
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(pluginClassLoader);
            Resource[] mapperXmlResources = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "mapper/*Mapper.xml");

            for (Resource mapperXmlResource : Arrays.asList(mapperXmlResources)) {
                if (mapperXmlResource != null && mapperXmlResource.getFilename().endsWith("Mapper.xml")) {
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
        ClassLoader pluginClassLoader = super.pluginManager.getPluginClassLoader(pluginId);
        SqlSessionFactory sqlSessionFactory = super.beanFactory.getBean(SqlSessionFactory.class);
        Configuration configuration = sqlSessionFactory.getConfiguration();
        Resources.setDefaultClassLoader(pluginClassLoader);

        Field entityTableMapField = ReflectionUtils.findField(EntityHelper.class, "entityTableMap");
        ReflectionUtils.makeAccessible(entityTableMapField);
        Map<Class<?>, EntityTable> entityTableMap = (Map<Class<?>, EntityTable>) ReflectionUtils.getField(entityTableMapField, null);

        Field loadedResourcesField = ReflectionUtils.findField(configuration.getClass(), "loadedResources");
        ReflectionUtils.makeAccessible(loadedResourcesField);
        Set<String> loadedResources = (Set<String>) ReflectionUtils.getField(loadedResourcesField, configuration);

        MapperRegistry mapperRegistry = configuration.getMapperRegistry();
        Field knownMappersField = ReflectionUtils.findField(mapperRegistry.getClass(), "knownMappers");
        ReflectionUtils.makeAccessible(knownMappersField);
        Map<Class<?>, MapperProxyFactory<?>> knownMappers = (Map<Class<?>, MapperProxyFactory<?>>) ReflectionUtils.getField(knownMappersField, mapperRegistry);

        Field classCacheField = ReflectionUtils.findField(MsUtil.class, "CLASS_CACHE");
        ReflectionUtils.makeAccessible(classCacheField);
        Cache cache = (Cache) ReflectionUtils.getField(classCacheField, null);

        for (Class<?> mapperClass : this.getMapperClassList(pluginId)) {
            try {
                applicationContext.removeBeanDefinition(mapperClass.getName());
                super.destroyBean(mapperClass);

                loadedResources.remove("namespace:" + mapperClass.getName());
                loadedResources.remove("interface " + mapperClass.getName());

                knownMappers.remove(mapperClass);

                Type[] baseMapperClass = mapperClass.getGenericInterfaces();
                if (baseMapperClass != null && baseMapperClass.length > 0) {
                    Class<?> entityClass = (Class<?>) ((ParameterizedType) baseMapperClass[0]).getActualTypeArguments()[0];
                    entityTableMap.remove(entityClass);
                }

                Collection<String> mappedStatementNames = configuration.getMappedStatementNames();
                Set<String> filterSet = mappedStatementNames.stream().filter(i -> i.contains(mapperClass.getSimpleName())).collect(Collectors.toSet());
                if (!filterSet.isEmpty()) {
                    Field mappedStatementField = ReflectionUtils.findField(configuration.getClass(), "mappedStatements");
                    ReflectionUtils.makeAccessible(mappedStatementField);
                    Map<String, MappedStatement>  mappedStatementMap = (Map<String, MappedStatement>) ReflectionUtils.getField(mappedStatementField, configuration);
                    for (String key : filterSet) {
                        mappedStatementMap.remove(key);
                    }
                }

                Collection<String> resultMapNames = configuration.getResultMapNames();
                List<String> filterCollection  = resultMapNames.stream().filter(i -> i.contains(mapperClass.getSimpleName())).collect(Collectors.toList());
                if (!filterCollection.isEmpty()) {
                    Field resultMapField = ReflectionUtils.findField(configuration.getClass(), "resultMaps");
                    ReflectionUtils.makeAccessible(resultMapField);
                    Map<String, ResultMap> resultMaps = (Map<String, ResultMap>) ReflectionUtils.getField(resultMapField, configuration);
                    for (String key : filterCollection) {
                        resultMaps.remove(key);
                    }
                }

                cache.removeObject(mapperClass.getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(pluginClassLoader);
            Resource[] mapperXmlResources = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "mapper/*Mapper.xml");

            for (Resource mapperXmlResource : Arrays.asList(mapperXmlResources)) {
                if(mapperXmlResource != null && mapperXmlResource.getFilename().endsWith("Mapper.xml")) {
                    loadedResources.remove(mapperXmlResource.toString());
                }
            }
        } finally {
            Resources.setDefaultClassLoader(ClassUtils.getDefaultClassLoader());
        }

    }

    private List<Class<?>> getMapperClassList(String pluginId) throws Exception {
        return super.getPluginClasses(pluginId).stream().filter(clazz -> BaseMapper.class.isAssignableFrom(clazz)).collect(Collectors.toList());
    }

}
