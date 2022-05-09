package com.light.hexo.core.admin.service.impl;

import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.core.admin.component.DefaultFileService;
import com.light.hexo.mapper.mapper.AttachmentMapper;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.model.Attachment;
import com.light.hexo.core.admin.service.AttachmentService;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.file.FileRequest;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.request.AttachmentRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: AttachmentServiceImpl
 * @ProjectName hexo-boot
 * @Description: 附件 Service 实现
 * @DateTime 2020/9/10 16:07
 */
@Service
public class AttachmentServiceImpl extends BaseServiceImpl<Attachment> implements AttachmentService {

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private DefaultFileService defaultFileService;

    @Override
    public BaseMapper<Attachment> getBaseMapper() {
        return this.attachmentMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {

        AttachmentRequest attachmentRequest = (AttachmentRequest) request;
        Example example = new Example(Attachment.class);
        Example.Criteria criteria = example.createCriteria();

        String filename = attachmentRequest.getFilename();
        if (StringUtils.isNotBlank(filename)) {
            criteria.andLike("filename", filename + "%");
        }

        Integer position = attachmentRequest.getPosition();
        if (position != null) {
            criteria.andEqualTo("position", position);
        }

        Integer fileType = attachmentRequest.getFileType();
        if (fileType != null) {
            criteria.andEqualTo("fileType", fileType);
        }

        example.orderBy("id").desc();
        return example;
    }

    @Override
    public void removeAttachmentBatch(List<String> idStrList) throws GlobalException {

        if (CollectionUtils.isEmpty(idStrList)) {
            return;
        }

        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());
        for (Integer id : idList) {
            Attachment attachment = super.findById(id);
            if (attachment != null) {
                FileRequest fileRequest = new FileRequest();
                fileRequest.setFilename(attachment.getFilename())
                        .setFilePath(attachment.getFilePath())
                        .setFileUrl(attachment.getFileUrl());
                this.defaultFileService.remove(fileRequest, attachment.getPosition());
            }
        }

        super.removeBatch(idList);
    }

}
