package com.light.hexo.common.component.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

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
    private EventServiceFactory eventServiceFactory;

    private Queue eventQueue = new LinkedBlockingQueue();

    /**
     * 发送事件
     * @param baseEvent
     */
    @Async
    public void emit(BaseEvent baseEvent) {
        this.eventQueue.offer(baseEvent);
    }

    @Scheduled(fixedRate = 500)
    public void dealWithEvent() {

        Object obj = this.eventQueue.poll();
        if (obj != null) {
            BaseEvent event = (BaseEvent) obj;
            EventService eventService = this.eventServiceFactory.getInstance(event.getEventType());
            eventService.dealWithEvent(event);
        }
    }
}
