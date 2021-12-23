package com.light.hexo.business.admin.model.event;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: CategoryEvent
 * @ProjectName hexo-boot
 * @Description: 分类事件
 * @DateTime 2021/9/18, 0018 10:16
 */
@Setter
@Getter
@Accessors(chain = true)
public class CategoryEvent extends BaseEvent {

    @Override
    protected EventEnum getEventType() {
        return EventEnum.CATEGORY;
    }
}