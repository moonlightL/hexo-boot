package com.light.hexo.common.component.event;

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

    public ConfigEvent(Object source) {
        super(source);
    }

    @Override
    protected String getEventType() {
        return EventEnum.CONFIG.getType();
    }
}
