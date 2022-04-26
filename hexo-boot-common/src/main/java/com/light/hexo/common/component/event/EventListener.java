package com.light.hexo.common.component.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author MoonlightL
 * @ClassName: EventListener
 * @ProjectName hexo-boot
 * @Description: 事件监听器
 * @DateTime 2022/4/25, 0025 10:27
 */
@Component
public class EventListener implements ApplicationListener<BaseEvent> {

    @Autowired
    private EventServiceFactory eventServiceFactory;

    private static ExecutorService[] serviceArray;

    static {
        int length = 5;
        serviceArray = new ExecutorService[length];
        for (int i = 0; i < length; i++) {
            serviceArray[i] = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    public void onApplicationEvent(BaseEvent event) {
        if (event != null) {
            int index = Math.abs(event.getEventType().hashCode()) % serviceArray.length;
            serviceArray[index].execute(() -> {
                EventService eventService = this.eventServiceFactory.getInstance(event.getEventType());
                eventService.dealWithEvent(event);
            });
        }
    }
}
