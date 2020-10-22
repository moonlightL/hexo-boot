package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.Message;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.exception.GlobalException;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: MessageService
 * @ProjectName hexo-boot
 * @Description: 消息 Service
 * @DateTime 2020/9/18 15:21
 */
public interface MessageService extends BaseService<Message>, EventService {

    /**
     * 查询消息列表
     * @param readStatus
     * @return
     * @throws GlobalException
     */
    List<Message> listMessages(int readStatus) throws GlobalException;

    /**
     * 批量修改读取状态
     * @param messageList
     * @throws GlobalException
     */
    void updateStatusBatch(List<Message> messageList) throws GlobalException;
}
