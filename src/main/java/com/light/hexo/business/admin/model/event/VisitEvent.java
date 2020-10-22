package com.light.hexo.business.admin.model.event;

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

    @Override
    protected EventEnum getEventType() {
        return EventEnum.VISIT;
    }

    public VisitEvent(String ipAddress, String browser) {
        this.ipAddress = ipAddress;
        this.browser = browser;
    }
}