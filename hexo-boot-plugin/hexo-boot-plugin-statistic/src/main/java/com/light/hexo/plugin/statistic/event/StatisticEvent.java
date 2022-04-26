package com.light.hexo.plugin.statistic.event;

import com.light.hexo.common.component.event.BaseEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: StatisticEvent
 * @ProjectName hexo-boot
 * @Description: 统计事件
 * @DateTime 2022/4/25, 0025 23:08
 */
@Setter
@Getter
@Accessors(chain = true)
public class StatisticEvent extends BaseEvent {

    private String ip;

    private String url;

    private String browser;

    public StatisticEvent(Object source) {
        super(source);
    }

    public StatisticEvent(Object source, String ip, String url, String browser) {
        super(source);
        this.ip = ip;
        this.url = url;
        this.browser = browser;
    }

    @Override
    protected String getEventType() {
        return StatisticConstant.EVENT_TYPE;
    }
}
