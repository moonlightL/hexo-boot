package com.light.hexo.common.component.event;

/**
 * @Author MoonlightL
 * @ClassName: EventService
 * @ProjectName hexo-boot
 * @Description: 事件处理 Service
 * @DateTime 2020/9/16 10:58
 */
public interface EventService {

    String getEventType();

    void dealWithEvent(BaseEvent event);
}
