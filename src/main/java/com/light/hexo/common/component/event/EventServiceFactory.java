package com.light.hexo.common.component.event;

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
public class EventServiceFactory implements ApplicationContextAware {

    private static Map<Integer, EventService> eventServiceMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, EventService> serviceMap = applicationContext.getBeansOfType(EventService.class);
        eventServiceMap = new HashMap<>(serviceMap.size());
        serviceMap.forEach((k, v) -> eventServiceMap.put(v.getEventType().getCode(), v));
    }

    /**
     * 获取实例
     * @param eventEnum
     * @return
     */
    public EventService getInstance(EventEnum eventEnum) {
        return eventServiceMap.get(eventEnum.getCode());
    }
}
