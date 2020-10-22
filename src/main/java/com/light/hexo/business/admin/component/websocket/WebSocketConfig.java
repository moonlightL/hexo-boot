package com.light.hexo.business.admin.component.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @Author MoonlightL
 * @ClassName: WebSocketConfig
 * @ProjectName hexo-boot
 * @Description: websocket 配置
 * @DateTime 2020/9/18 15:41
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageServer(), "/messageServer")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler messageServer() {
        return new MessageServer();
    }

}