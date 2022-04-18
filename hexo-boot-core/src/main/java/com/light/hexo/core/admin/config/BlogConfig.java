package com.light.hexo.core.admin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author MoonlightL
 * @ClassName: BlogConfig
 * @ProjectName hexo-boot
 * @Description: 博客扩展属性
 * @DateTime 2020/12/2 9:49
 */
@Setter
@Getter
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "blog")
public class BlogConfig {

    /**
     * 博客主目录
     */
    private String homeDir;

    /**
     * 博客主题目录
     */
    private String themeDir;

    /**
     * 附件目录
     */
    private String attachmentDir;

    /**
     * 日志目录
     */
    private String logDir;

    /**
     * 插件目录
     */
    private String pluginDir;

    /**
     * 版本
     */
    private String version;
}
