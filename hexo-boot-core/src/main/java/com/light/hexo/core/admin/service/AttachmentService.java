package com.light.hexo.core.admin.service;

import com.light.hexo.common.base.BaseService;
import com.light.hexo.mapper.model.Attachment;
import com.light.hexo.common.exception.GlobalException;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: AttachmentService
 * @ProjectName hexo-boot
 * @Description: 附件 Service
 * @DateTime 2020/9/10 16:07
 */
public interface AttachmentService extends BaseService<Attachment> {

    /**
     * 批量删除附件
     * @param idStrList
     * @throws GlobalException
     */
    void removeAttachmentBatch(List<String> idStrList) throws GlobalException;

}
