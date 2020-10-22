package com.light.hexo.business.admin.model.event;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: ConfigEvent
 * @ProjectName hexo-boot
 * @Description: 配置修改事件
 * @DateTime 2020/9/16 11:30
 */
@Setter
@Getter
@Accessors(chain = true)
public class ConfigEvent extends BaseEvent {

    @Override
    protected EventEnum getEventType() {
        return EventEnum.CONFIG;
    }
}
