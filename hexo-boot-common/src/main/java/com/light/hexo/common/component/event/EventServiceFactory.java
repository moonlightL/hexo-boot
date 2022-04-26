package com.light.hexo.common.component.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: EventServiceFactory
 * @ProjectName hexo-boot
 * @Description: 事件处理 Service 工厂
 * @DateTime 2020/9/16 10:59
 */
@Component
@Slf4j
public class EventServiceFactory implements ApplicationContextAware {

    private final Map<String, EventService> eventServiceMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, EventService> serviceMap = applicationContext.getBeansOfType(EventService.class);
        serviceMap.forEach((k, v) -> eventServiceMap.put(v.getEventType(), v));
    }

    /**
     * 获取实例
     * @param eventType
     * @return
     */
    public EventService getInstance(String eventType) {
        return eventServiceMap.get(eventType);
    }

    /**
     * 添加实例
     * @param eventService
     */
    public void addEventService(EventService eventService) {
        log.info("=============== EventServiceFactory.addEventService ==================");
        eventServiceMap.put(eventService.getEventType(), eventService);
    }

    /**
     * 移除实例
     * @param eventService
     */
    public void removeEventService(EventService eventService) {
        log.info("=============== EventServiceFactory.removeEventService ==================");
        eventServiceMap.remove(eventService.getEventType());
    }
}
