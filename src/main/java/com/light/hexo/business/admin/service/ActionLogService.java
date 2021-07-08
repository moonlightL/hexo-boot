package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.ActionLog;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.exception.GlobalException;

/**
 * @Author MoonlightL
 * @ClassName: ActionLogService
 * @ProjectName hexo-boot
 * @Description: 操作日志 Service
 * @DateTime 2021/7/7 17:52
 */
public interface ActionLogService extends BaseService<ActionLog>, EventService {

    /**
     * 日志详情
     * @param id
     * @return
     * @throws GlobalException
     */
    ActionLog getActionLogInfo(Integer id) throws GlobalException;
}
