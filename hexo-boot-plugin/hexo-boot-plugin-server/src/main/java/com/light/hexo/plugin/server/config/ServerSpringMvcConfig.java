package com.light.hexo.plugin.server.config;

import com.light.hexo.common.config.SpringMvcConfig;
import com.light.hexo.plugin.server.interceptor.ServerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * @Author MoonlightL
 * @ClassName: ServerSpringMvcConfig
 * @ProjectName hexo-boot
 * @Description: springmvc 配置
 * @DateTime 2022/4/24, 0024 11:27
 */
@Configuration
public class ServerSpringMvcConfig extends SpringMvcConfig {

    @Autowired
    private ServerInterceptor serverInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serverInterceptor)
                .addPathPatterns("/plugin/server/**")
                .excludePathPatterns("/error");
    }
}
