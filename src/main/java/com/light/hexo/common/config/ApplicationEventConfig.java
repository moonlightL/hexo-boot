package com.light.hexo.common.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * @Author MoonlightL
 * @ClassName: ApplicationEventConfig
 * @ProjectName hexo-boot
 * @Description: 事件相关配置
 * @DateTime 2020/7/29 17:14
 */
@Configuration
public class ApplicationEventConfig {

    @Bean
    public ApplicationEventPublisher createApplicationEventPublisher() {
        return new AnnotationConfigWebApplicationContext();
    }
}
