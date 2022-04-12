package com.light.hexo.component.websocket;

import com.light.hexo.mapper.model.Message;
import com.light.hexo.core.admin.service.MessageService;
import com.light.hexo.common.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: MessageServer
 * @ProjectName hexo-boot
 * @Description: 消息 websocket
 * @DateTime 2020/9/18 15:47
 */
public class MessageServer extends TextWebSocketHandler {

    private WebSocketSession webSocketSession;

    @Autowired
    private MessageService messageService;

    /**
     * 建立连接
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        List<Message> messageList = this.messageService.listMessages(0);
        if (!CollectionUtils.isEmpty(messageList)) {
            this.messageService.updateStatusBatch(messageList);
            session.sendMessage(new TextMessage(JsonUtil.obj2String(messageList)));
        } else {
            session.sendMessage(new TextMessage(JsonUtil.obj2String(new ArrayList<>())));
        }

        this.webSocketSession = session;
    }

    /**
     * 断开连接
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        this.webSocketSession = null;
    }

    /**
     * 处理消息
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleMessage(session, message);
        this.broadcast();
    }

    public void broadcast() {
        if (this.webSocketSession == null) {
            return;
        }

        List<Message> messageList = this.messageService.listMessages(0);
        if (!CollectionUtils.isEmpty(messageList)) {
            this.messageService.updateStatusBatch(messageList);
            try {
                this.webSocketSession.sendMessage(new TextMessage(JsonUtil.obj2String(messageList)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
