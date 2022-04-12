package com.light.hexo.core.admin.service.impl;

import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.component.MessagePushService;
import com.light.hexo.mapper.mapper.MessageMapper;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.model.Message;
import com.light.hexo.mapper.model.event.MessageEvent;
import com.light.hexo.core.admin.service.MessageService;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import com.light.hexo.common.exception.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: MessageServiceImpl
 * @ProjectName hexo-boot
 * @Description: 消息 Service 实现
 * @DateTime 2020/9/18 15:22
 */
@Service
public class MessageServiceImpl extends BaseServiceImpl<Message> implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessagePushService messagePushService;

    @Override
    public BaseMapper<Message> getBaseMapper() {
        return this.messageMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        return null;
    }

    @Override
    public List<Message> listMessages(int readStatus) throws GlobalException {
        Example example = new Example(Message.class);
        example.createCriteria().andEqualTo("read", readStatus);
        return this.getBaseMapper().selectByExample(example);
    }

    @Override
    public void updateStatusBatch(List<Message> messageList) throws GlobalException {
        List<Integer> idList = messageList.stream().map(Message::getId).collect(Collectors.toList());
        Example example = new Example(Message.class);
        example.createCriteria().andIn("id", idList);
        Message message = new Message();
        message.setRead(true).setUpdateTime(LocalDateTime.now());
        this.getBaseMapper().updateByExampleSelective(message, example);
    }

    @Override
    public EventEnum getEventType() {
        return EventEnum.MESSAGE;
    }

    @Override
    public void dealWithEvent(BaseEvent event) {
        MessageEvent messageEvent = (MessageEvent) event;
        Message message = new Message();
        message.setRead(false)
               .setContent(messageEvent.getContent())
               .setMsgType(messageEvent.getType().getCode());
        super.saveModel(message);

        this.messagePushService.pushMessage();
    }
}
