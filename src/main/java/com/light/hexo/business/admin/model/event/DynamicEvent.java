package com.light.hexo.business.admin.model.event;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: DynamicEvent
 * @ProjectName hexo-boot
 * @Description: 动态事件
 * @DateTime 2021/6/24 11:32
 */
@Setter
@Getter
@Accessors(chain = true)
public class DynamicEvent extends BaseEvent {

    /**
     * 动态 id
     */
    private Integer id;

    public DynamicEvent(Integer id) {
        this.id = id;
    }

    @Override
    protected EventEnum getEventType() {
        return EventEnum.DYNAMIC;
    }
}
