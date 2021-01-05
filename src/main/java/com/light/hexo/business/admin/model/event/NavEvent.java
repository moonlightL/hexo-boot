package com.light.hexo.business.admin.model.event;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: NavEvent
 * @ProjectName hexo-boot
 * @Description: 导航修改事件
 * @DateTime 2021/1/5 10:01
 */
@Setter
@Getter
@Accessors(chain = true)
public class NavEvent extends BaseEvent {

    @Override
    protected EventEnum getEventType() {
        return EventEnum.NAV;
    }
}
