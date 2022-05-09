package com.light.hexo.common.request;

import com.light.hexo.mapper.model.ActionLog;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author MoonlightL
 * @ClassName: ActionLogRequest
 * @ProjectName hexo-boot
 * @Description: 操作日志请求对象
 * @DateTime 2021/7/7 18:34
 */
@Setter
@Getter
public class ActionLogRequest extends BaseRequest<ActionLog> {


    /**
     * 操作类型，参考 ActionEnum
     */
    private Integer actionType;
}
