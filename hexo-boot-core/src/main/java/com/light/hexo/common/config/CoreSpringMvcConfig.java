package com.light.hexo.common.config;

import com.light.hexo.config.BlogProperty;
import com.light.hexo.constant.ConfigEnum;
import com.light.hexo.core.admin.service.ConfigService;
import com.light.hexo.web.interceptor.InstallInterceptor;
import com.light.hexo.web.interceptor.UserInterceptor;
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
    private BlogProperty blogProperty;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String filePath = this.configService.getConfigValue(ConfigEnum.LOCAL_FILE_PATH.getName());
        String localFilePath = StringUtils.isNotBlank(filePath) ?
                filePath  + File.separator
                : this.blogProperty.getAttachmentDir();

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" +  localFilePath);

        registry.addResourceHandler("/cover/**")
                .addResourceLocations("file:" +  localFilePath + "cover/");

        registry.addResourceHandler("/theme/**")
                .addResourceLocations(
                        "file:" + this.blogProperty.getThemeDir(),
                        "classpath:/templates/theme/"
                );
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(installInterceptor)
                .addPathPatterns("/")
                .addPathPatterns("/admin")
                .addPathPatterns("/admin/login.html")
                .addPathPatterns("/admin/login.json")
                .excludePathPatterns("/error");

        registry.addInterceptor(visitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/admin/assets/**")
                .excludePathPatterns("/theme/**")
                .excludePathPatterns("/error");

        registry.addInterceptor(userInterceptor)
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
