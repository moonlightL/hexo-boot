package com.light.hexo.business.admin.model.event;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: TagEvent
 * @ProjectName hexo-boot
 * @Description: 标签事件
 * @DateTime 2021/9/18, 0018 11:43
 */
@Setter
@Getter
@Accessors(chain = true)
public class TagEvent extends BaseEvent {

    @Override
    protected EventEnum getEventType() {
        return EventEnum.TAG;
    }
}
