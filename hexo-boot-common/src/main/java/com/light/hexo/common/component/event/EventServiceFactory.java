package com.light.hexo.common.component.event;

import com.light.hexo.common.component.CommonServiceFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: EventServiceFactory
 * @ProjectName hexo-boot
 * @Description: 事件处理 Service 工厂
 * @DateTime 2020/9/16 10:59
 */
@Component
public class EventServiceFactory extends CommonServiceFactory<EventService> {

    private static final Map<String, EventService> SERVICE_MAP = new HashMap<>();

    @Override
    protected Map<String, EventService> getServiceMap() {
        return SERVICE_MAP;
    }
}
