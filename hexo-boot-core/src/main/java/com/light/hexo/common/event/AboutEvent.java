package com.light.hexo.common.event;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: AboutEvent
 * @ProjectName hexo-boot
 * @Description: 关于事件
 * @DateTime 2023/3/1, 0001 15:52
 */
@Setter
@Getter
@Accessors(chain = true)
public class AboutEvent extends BaseEvent {

    public AboutEvent(Object source) {
        super(source);
    }

    @Override
    protected String getEventType() {
        return EventEnum.ABOUT.getType();
    }
}
