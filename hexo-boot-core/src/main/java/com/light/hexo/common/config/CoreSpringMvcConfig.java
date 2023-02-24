package com.light.hexo.common.config;

import com.light.hexo.common.constant.ConfigEnum;
import com.light.hexo.core.admin.service.ConfigService;
import com.light.hexo.core.admin.web.interceptor.InstallInterceptor;
import com.light.hexo.core.admin.web.interceptor.UserInterceptor;
import com.light.hexo.core.portal.web.interceptor.VisitInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import java.io.File;

/**
 * @Author MoonlightL
 * @ClassName: CoreSpringMvcConfig
 * @ProjectName hexo-boot
 * @Description: 核心模块 springmvc 配置
 * @DateTime 2022/4/12, 0012 11:25
 */
@Configuration
public class CoreSpringMvcConfig extends SpringMvcConfig {

    @Autowired
    private UserInterceptor userInterceptor;

    @Autowired
    private VisitInterceptor visitInterceptor;

    @Autowired
    private InstallInterceptor installInterceptor;

    @Autowired
    private ConfigService configService;

    @Autowired
    private BlogConfig blogConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String filePath = this.configService.getConfigValue(ConfigEnum.LOCAL_FILE_PATH.getName());
        String localFilePath = StringUtils.isNotBlank(filePath) ?
                filePath  + File.separator
                : this.blogConfig.getAttachmentDir();

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" +  localFilePath);

        registry.addResourceHandler("/cover/**")
                .addResourceLocations("file:" +  localFilePath + "cover/");

        registry.addResourceHandler("/info/**")
                .addResourceLocations("file:" +  this.blogConfig.getAttachmentDir() + "info/");

        registry.addResourceHandler("/theme/**")
                .addResourceLocations(
                        "file:" + this.blogConfig.getThemeDir(),
                        "classpath:/templates/theme/"
                );

        registry.addResourceHandler("/ext/**")
                .addResourceLocations("classpath:/static/ext/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(this.installInterceptor)
                .addPathPatterns("/")
                .addPathPatterns("/admin")
                .addPathPatterns("/admin/login.html")
                .addPathPatterns("/admin/login.json")
                .excludePathPatterns("/error");

        registry.addInterceptor(this.visitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/**/*.ico",
                        "/**/*.js.map",
                        "/**/*.css.map",
                        "/**/*.json")
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/theme/**")
                .excludePathPatterns("/images/**")
                .excludePathPatterns("/error");

        registry.addInterceptor(this.userInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/assets/**",
                        "/admin/captcha.jpg",
                        "/admin/login.html",
                        "/admin/login.json",
                        "/admin/logout.json",
                        "/admin/install.html",
                        "/admin/install.json",
                        "/admin/postAuth.html",
                        "/error");
    }
}
