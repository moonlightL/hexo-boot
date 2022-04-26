package com.light.hexo.common.component.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author MoonlightL
 * @ClassName: EventPublisher
 * @ProjectName hexo-boot
 * @Description: 事件发送器
 * @DateTime 2020/9/16 11:16
 */
@Component
public class EventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 发送事件
     * @param baseEvent
     */
    @Async
    public void emit(BaseEvent baseEvent) {
        this.applicationEventPublisher.publishEvent(baseEvent);
    }

}
