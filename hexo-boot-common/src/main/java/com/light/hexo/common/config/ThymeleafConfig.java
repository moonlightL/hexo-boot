package com.light.hexo.common.config;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author MoonlightL
 * @ClassName: ThymeleafConfig
 * @ProjectName hexo-boot
 * @Description: Thymeleaf 配置
 * @DateTime 2020/12/1 16:10
 */
@Configuration
public class ThymeleafConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    public ITemplateResolver classPathTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setName("classPathTemplateResolver");
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        templateResolver.setCheckExistence(true);
        return templateResolver;
    }

    @Bean
    public ITemplateResolver fileTemplateResolver() {
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setName("fileTemplateResolver");
        templateResolver.setPrefix(System.getProperty("user.home") + "/.hexo-boot/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        templateResolver.setCheckExistence(true);
        return templateResolver;
    }

    @Bean(name = "springTemplateEngine")
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        Set<ITemplateResolver> templateResolvers = new HashSet<>();
        templateResolvers.add(this.classPathTemplateResolver());
        templateResolvers.add(this.fileTemplateResolver());
        templateEngine.setTemplateResolvers(templateResolvers);
        Set<IDialect> additionalDialects = new HashSet<>();
        additionalDialects.add(this.layoutDialect());
        templateEngine.setAdditionalDialects(additionalDialects);
        return templateEngine;
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
