package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.plugin.annotation.HexoBootWebSocket;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: WebSocketRegistry
 * @ProjectName hexo-boot
 * @Description: WebSocket 注册器
 * @DateTime 2022/5/19, 0019 10:55
 */
public class WebSocketRegistry extends AbstractModuleRegistry {

    private ApplicationContext applicationContext;

    private AbstractUrlHandlerMapping urlHandlerMapping;

    private Map<String, Object> handlerMap;

    public WebSocketRegistry(HexoBootPluginManager pluginManager) {
        super(pluginManager);
        this.applicationContext = super.pluginManager.getApplicationContext();
        this.urlHandlerMapping = (AbstractUrlHandlerMapping) this.applicationContext.getBean("beanNameHandlerMapping");
        Field handlerMapField = ReflectionUtils.findField(this.urlHandlerMapping.getClass(), "handlerMap", Map.class);
        handlerMapField.setAccessible(true);
        try {
            this.handlerMap = (Map<String, Object>) handlerMapField.get(this.urlHandlerMapping);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(String pluginId) {
        for (Class<?> clazz : super.getPluginClasses(pluginId)) {
            HexoBootWebSocket webSocket = clazz.getAnnotation(HexoBootWebSocket.class);
            if (webSocket != null && webSocket.url() != null) {
                WebSocketHandler wsHandler = (WebSocketHandler) this.applicationContext.getBean(webSocket.handlerClass().getName());
                WebSocketHttpRequestHandler webSocketHttpRequestHandler = new WebSocketHttpRequestHandler(wsHandler);
                if (!webSocket.interceptorClass().equals(Void.class)) {
                    HandshakeInterceptor handshakeInterceptor = (HandshakeInterceptor) this.applicationContext.getBean(webSocket.interceptorClass().getName());
                    webSocketHttpRequestHandler.getHandshakeInterceptors().add(handshakeInterceptor);
                }
                this.handlerMap.put(webSocket.url(), webSocketHttpRequestHandler);
            }
        }
    }

    @Override
    public void unRegister(String pluginId) {
        for (Class<?> clazz : super.getPluginClasses(pluginId)) {
            HexoBootWebSocket webSocket = clazz.getAnnotation(HexoBootWebSocket.class);
            if (webSocket != null && webSocket.url() != null) {
                this.handlerMap.remove(webSocket.url());
            }
        }
    }
}
