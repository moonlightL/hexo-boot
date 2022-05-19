package com.light.hexo.core.admin.component.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @Author MoonlightL
 * @ClassName: MessageSocketConfig
 * @ProjectName hexo-boot
 * @Description: websocket 配置
 * @DateTime 2020/9/18 15:41
 */
@Configuration
public class MessageSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageWebSocketHandler(), "/messageServer")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler messageWebSocketHandler() {
        return new MessageWebSocketHandler();
    }

}