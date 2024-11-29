package com.light.hexo.core.admin.web.listener;

import com.light.hexo.common.util.RequestUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

/**
 * @Author MoonlightL
 * @ClassName: BrowserOpenListener
 * @Description: 启动浏览器
 * @DateTime 2024/11/29 15:36
 */
@Component
public class BrowserOpenListener {

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser(ApplicationReadyEvent event) {
        try {
            ConfigurableApplicationContext context = event.getApplicationContext();
            String hostIp = RequestUtil.getHostIp();
            String port = context.getEnvironment().getProperty("server.port");
            Runtime.getRuntime().exec("cmd /c start http://" + hostIp + ":" + port);
        } catch (IOException e) {

        }
    }
}
