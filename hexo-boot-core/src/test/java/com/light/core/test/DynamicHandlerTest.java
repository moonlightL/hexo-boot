package com.light.core.test;

import com.light.hexo.HexoBootApplication;
import com.light.hexo.common.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
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
 * @ClassName: DynamicHandlerTest
 * @ProjectName hexo-boot
 * @Description: 动态 mapping 测试
 * @DateTime 2022/4/13, 0013 14:46
 */
@SpringBootTest(classes = HexoBootApplication.class)
public class DynamicHandlerTest {

    @Test
    public void test() throws Exception {
        ApplicationContext applicationContext = SpringContextUtil.applicationContext;
        ServerTest ServerTest = null;
        try {
            ServerTest = applicationContext.getBean(DynamicHandlerTest.ServerTest.class);
        } catch (BeansException e) {
            e.printStackTrace();
        }

        Class<?> clazz = ServerTest.class;
        String beanName = StringUtils.uncapitalize(clazz.getName());
        beanName = beanName.substring(beanName.lastIndexOf(".") + 1);
        beanName = StringUtils.uncapitalize(beanName);

        if (ServerTest == null) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
            beanDefinition.setScope("singleton");
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
            defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinition);
        }

        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        if (requestMappingHandlerMapping != null) {
            Method method = ReflectionUtils.findMethod(clazz, "work");
            PatternsRequestCondition patterns = new PatternsRequestCondition("plugin/getServerInfo.json");
            RequestMethodsRequestCondition condition = new RequestMethodsRequestCondition(RequestMethod.GET);
            RequestMappingInfo mappingInfo = new RequestMappingInfo(patterns, condition, null, null, null, null, null);
            requestMappingHandlerMapping.registerMapping(mappingInfo, clazz.newInstance(), method);
        }

        System.in.read();

    }


    static class ServerTest {

        public void work() {
            System.out.println("work");
        }

    }
}
