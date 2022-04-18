package com.light.hexo.common.config;

import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @Author MoonlightL
 * @ClassName: PluginConfig
 * @ProjectName hexo-boot
 * @Description: 插件配置
 * @DateTime 2022/4/12, 0012 18:01
 */
@Configuration
public class PluginConfig {

    @Bean
    @Lazy
    public SpringPluginManager pluginManager() {
        return new SpringPluginManager();
    }

}
