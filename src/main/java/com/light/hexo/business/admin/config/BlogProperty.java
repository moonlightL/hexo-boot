package com.light.hexo.business.admin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author MoonlightL
 * @ClassName: BlogProperty
 * @ProjectName hexo-boot
 * @Description: 博客扩展属性
 * @DateTime 2020/12/2 9:49
 */
@Setter
@Getter
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "blog")
public class BlogProperty {

    private String homeDir;

    private String themeDir;

    private String attachmentDir;

    private String logDir;

    private String version;
}
