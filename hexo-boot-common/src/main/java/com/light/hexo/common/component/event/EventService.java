package com.light.hexo.common.component.event;

import com.light.hexo.common.component.CommonService;

/**
 * @Author MoonlightL
 * @ClassName: EventService
 * @ProjectName hexo-boot
 * @Description: 事件处理 Service
 * @DateTime 2020/9/16 10:58
 */
public interface EventService extends CommonService {

    void dealWithEvent(BaseEvent event);
}
