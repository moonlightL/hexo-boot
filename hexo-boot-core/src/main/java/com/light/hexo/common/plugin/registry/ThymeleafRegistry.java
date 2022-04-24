package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import lombok.SneakyThrows;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import java.util.*;

/**
 * @Author MoonlightL
 * @ClassName: ThymeleafRegistry
 * @ProjectName hexo-boot
 * @Description: 前端模板注册器
 * @DateTime 2022/4/21, 0021 16:31
 */
public class ThymeleafRegistry extends AbstractModuleRegistry {

    private Map<String, ITemplateResolver> pluginTemplateResolver = new HashMap<>();

    public ThymeleafRegistry(HexoBootPluginManager pluginManager) {
        super(pluginManager);
    }

    @Override
    public void register(String pluginId) {

        SpringTemplateEngine springTemplateEngine = super.beanFactory.getBean(SpringTemplateEngine.class);
        Set<ITemplateResolver> oldTemplateResolver = springTemplateEngine.getTemplateResolvers();

        ContentNegotiatingViewResolver negotiatingViewResolver = this.beanFactory.getBean(ContentNegotiatingViewResolver.class);
        List<ViewResolver> viewResolvers = negotiatingViewResolver.getViewResolvers();
        for (ViewResolver viewResolver : viewResolvers) {
            if (viewResolver instanceof ThymeleafViewResolver) {
                ThymeleafViewResolver tmpViewResolver = (ThymeleafViewResolver) viewResolver;
                tmpViewResolver.setTemplateEngine(this.createSpringTemplateEngine(pluginId, oldTemplateResolver));
            }
        }
    }

    @Override
    public void unRegister(String pluginId) {
        String resolverName = this.templateResolverName(pluginId);
        if (this.pluginTemplateResolver.containsKey(resolverName)) {
            this.pluginTemplateResolver.remove(resolverName);
        }
    }

    @SneakyThrows
    private SpringTemplateEngine createSpringTemplateEngine(String pluginId, Set<ITemplateResolver> oldTemplateResolver) {

        ApplicationContext applicationContext = super.pluginManager.getApplicationContext();
        PluginWrapper pluginWrapper = this.pluginManager.getPlugin(pluginId);
        ClassLoader pluginClassLoader = pluginWrapper.getPluginClassLoader();
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader(pluginClassLoader);
        Resource resource = defaultResourceLoader.getResource("classpath:/templates/");
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setName(this.templateResolverName(pluginId));
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix(resource.getURI().toString());
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        templateResolver.setCheckExistence(true);
        this.pluginTemplateResolver.put(templateResolver.getName(), templateResolver);

        Set<ITemplateResolver> newTemplateResolvers = new HashSet<>();
        newTemplateResolvers.addAll(oldTemplateResolver);
        newTemplateResolvers.addAll(this.pluginTemplateResolver.values());

        SpringTemplateEngine newSpringTemplateEngine = new SpringTemplateEngine();
        newSpringTemplateEngine.setEnableSpringELCompiler(true);
        newSpringTemplateEngine.setTemplateResolvers(newTemplateResolvers);
        newSpringTemplateEngine.addDialect(this.beanFactory.getBean(LayoutDialect.class));

        return newSpringTemplateEngine;
    }

    private String templateResolverName(String pluginId) {
        return pluginId + "-templateResolverName";
    }
}
