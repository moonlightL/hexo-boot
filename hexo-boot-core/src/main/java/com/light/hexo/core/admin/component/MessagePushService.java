package com.light.hexo.component;

import com.light.hexo.component.websocket.MessageServer;
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
@DependsOn("messageServer")
public class MessagePushService {

    @Autowired
    private MessageServer messageServer;

    public void pushMessage() {
        this.messageServer.broadcast();
    }
}
