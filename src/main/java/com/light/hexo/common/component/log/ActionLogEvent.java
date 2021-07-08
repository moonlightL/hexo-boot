package com.light.hexo.common.component.log;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: ActionLogEvent
 * @ProjectName hexo-boot
 * @Description: 操作日志事件
 * @DateTime 2021/7/7 17:40
 */
@Setter
@Getter
@Accessors(chain = true)
public class ActionLogEvent extends BaseEvent {

    private String methodName;

    private String methodParam;

    private String ipAddress;

    private String browser;

    private String remark;

    private Integer actionType;

    private LocalDateTime createTime;

    protected EventEnum getEventType() {
        return EventEnum.LOG;
    }
}
