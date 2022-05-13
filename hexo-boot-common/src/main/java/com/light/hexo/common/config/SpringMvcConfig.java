package com.light.hexo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: SpringMvcConfig
 * @ProjectName hexo-boot
 * @Description: Spring mvc 配置
 * @DateTime 2020/7/30 11:04
 */
@Configuration
@DependsOn("flywayConfig")
public class SpringMvcConfig implements WebMvcConfigurer {

    @Bean
    public ContentNegotiatingViewResolver contentNegotiatingViewResolver(){
        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        List<View> list = new ArrayList<>();
        list.add(mappingJackson2JsonView());
        viewResolver.setDefaultViews(list);
        return viewResolver;
    }

    @Bean
    public MappingJackson2JsonView mappingJackson2JsonView(){
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        jsonView.setBeanName("mappingJackson2JsonView");
        return jsonView;
    }
}
