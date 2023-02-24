package com.light.hexo.common.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
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
     * 版本号，此处硬编码避免项目启动引用外部文件（application.yml），导致版本数据无法更新
     */
    private String version = "4.1.0";

    public Integer getVersionCode() {
        if (StringUtils.isNotBlank(version)) {
            return Integer.valueOf(version.replaceAll("\\.", ""));
        }
        return 0;
    }
}
