package com.light.hexo.common.event;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: VisitEvent
 * @ProjectName hexo-boot
 * @Description: 访问事件
 * @DateTime 2020/9/16 13:40
 */
@Setter
@Getter
@Accessors(chain = true)
public class VisitEvent extends BaseEvent {

    private String ipAddress;

    private String browser;

    public VisitEvent(Object source, String ipAddress, String browser) {
        super(source);
        this.ipAddress = ipAddress;
        this.browser = browser;
    }

    @Override
    protected String getEventType() {
        return EventEnum.VISIT.getType();
    }


}