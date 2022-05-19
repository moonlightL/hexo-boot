package com.light.hexo.core.admin.component;

import com.light.hexo.core.admin.component.websocket.MessageWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * @Author MoonlightL
 * @ClassName: MessagePushService
 * @ProjectName hexo-boot
 * @Description: 消息推送
 * @DateTime 2020/9/23 15:11
 */
@Component
@DependsOn("messageWebSocketHandler")
public class MessagePushService {

    @Autowired
    private MessageWebSocketHandler messageWebSocketHandler;

    public void pushMessage() {
        this.messageWebSocketHandler.broadcast();
    }
}
