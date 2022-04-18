package com.light.hexo.plugin.server.config;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @Author MoonlightL
 * @ClassName: ServerConfig
 * @ProjectName hexo-boot
 * @Description: 配置
 * @DateTime 2022/4/13, 0013 18:07
 */
@Configuration
public class ServerConfig implements WebMvcRegistrations, WebMvcConfigurer {

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return WebMvcRegistrations.super.getRequestMappingHandlerMapping();
    }
}
