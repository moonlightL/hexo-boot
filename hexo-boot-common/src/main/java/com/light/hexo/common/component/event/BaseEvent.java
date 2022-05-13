package com.light.hexo.common.component.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Author MoonlightL
 * @ClassName: BaseEvent
 * @ProjectName hexo-boot
 * @Description: 事件基类
 * @DateTime 2020/9/16 10:51
 */
public abstract class BaseEvent extends ApplicationEvent {

    public BaseEvent(Object source) {
        super(source);
    }

    protected abstract String getEventType();
}
