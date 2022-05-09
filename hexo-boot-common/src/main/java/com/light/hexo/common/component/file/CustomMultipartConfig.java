package com.light.hexo.common.component.file;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

/**
 * @Author MoonlightL
 * @ClassName: CustomMultipartConfig
 * @ProjectName hexo-boot
 * @Description: 文件上传配置
 * @DateTime 2022/3/31, 0031 9:55
 */
@Configuration
public class CustomMultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //允许上传的文件最大值
        factory.setMaxFileSize(DataSize.ofBytes(100 * 1024 * 1024));
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.ofBytes(100 * 1024 * 1024));
        return factory.createMultipartConfig();
    }
}
