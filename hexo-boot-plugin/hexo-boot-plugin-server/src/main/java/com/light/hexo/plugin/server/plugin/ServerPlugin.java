package com.light.hexo.plugin.server.plugin;

import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @Author MoonlightL
 * @ClassName: ServerPlugin
 * @ProjectName hexo-boot
 * @Description: 生命周期
 * @DateTime 2022/4/12, 0012 18:30
 */
public class ServerPlugin extends Plugin {

    public ServerPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("ServerLiveTime.start()");

        ApplicationContext applicationContext = ((SpringPluginManager) super.getWrapper().getPluginManager()).getApplicationContext();

        System.out.println("applicationContext: " + applicationContext);

        Class<?> clazz = null;
        try {
            clazz = Class.forName("com.light.hexo.plugin.server.controller.ServerController");
            String beanName = StringUtils.uncapitalize(clazz.getName());
            beanName = beanName.substring(beanName.lastIndexOf(".") + 1);
            beanName = StringUtils.uncapitalize(beanName);

            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
            beanDefinition.setScope("singleton");
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
            defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinition);

            RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
            if (requestMappingHandlerMapping != null) {
                Method method = ReflectionUtils.findMethod(clazz, "getServerData");
                PatternsRequestCondition patterns = new PatternsRequestCondition("plugin/getServerData.json");
                RequestMethodsRequestCondition condition = new RequestMethodsRequestCondition(RequestMethod.GET);
                RequestMappingInfo mappingInfo = new RequestMappingInfo(patterns, condition, null, null, null, null, null);
                requestMappingHandlerMapping.registerMapping(mappingInfo, clazz.newInstance(), method);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.out.println("ServerPlugin.stop()");
    }

    @Override
    public void delete() {
        System.out.println("ServerPlugin.delete()");
    }

    @Extension
    public static class ServerExtension implements HexoBootExtensionPoint {

        @Override
        public void action(ApplicationContext applicationContext) {
            System.out.println("============ServerExtension action============");
        }

        @Override
        public void retreat(ApplicationContext applicationContext) {
            System.out.println("============ServerExtension retreat============");
        }
    }

}
