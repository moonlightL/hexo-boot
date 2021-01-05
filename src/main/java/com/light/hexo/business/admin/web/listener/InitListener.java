package com.light.hexo.business.admin.web.listener;

import com.light.hexo.business.admin.model.Config;
import com.light.hexo.business.admin.model.Nav;
import com.light.hexo.business.admin.service.ConfigService;
import com.light.hexo.business.admin.service.NavService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: InitListener
 * @ProjectName hexo-boot
 * @Description: 初始化和加载全局变量(bean加载完成后执行)
 * @DateTime 2020/10/1 10:57
 */
@Component
@Slf4j
public class InitListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ConfigService configService;

    @Autowired
    private NavService navService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        WebApplicationContext webApplicationContext = (WebApplicationContext)contextRefreshedEvent.getApplicationContext();
        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext == null) {
            log.info("==========ConfigMapInitListener 获取 ServletContext 为空===========");
            return;
        }

        List<Config> configList = this.configService.findAll();
        Map<String, String> configMap = configList.stream().collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue,  (v1, v2) -> v2));
        servletContext.setAttribute("configMap", configMap);
        log.info("==========ConfigMapInitListener 初始化[全局参数]===========");

        this.navService.initNav(servletContext);
        log.info("==========ConfigMapInitListener 初始化[导航数据]===========");
    }
}