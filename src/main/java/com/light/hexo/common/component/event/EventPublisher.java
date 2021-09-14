package com.light.hexo.common.component.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private Queue<BaseEvent> eventQueue = new LinkedBlockingQueue<>();

    /**
     * 发送事件
     * @param baseEvent
     */
    @Async
    public void emit(BaseEvent baseEvent) {
        this.eventQueue.offer(baseEvent);
    }

    @Scheduled(fixedRate = 1000)
    public void dealWithEvent() {
        BaseEvent event = this.eventQueue.poll();
        if (event != null) {
            this.threadPoolTaskExecutor.execute(() -> {
                EventService eventService = this.eventServiceFactory.getInstance(event.getEventType());
                eventService.dealWithEvent(event);
            });
        }
    }
}
