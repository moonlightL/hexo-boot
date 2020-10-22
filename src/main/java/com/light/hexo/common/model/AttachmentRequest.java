package com.light.hexo.common.model;

import com.light.hexo.business.admin.model.Attachment;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

/**
 * @Author MoonlightL
 * @ClassName: AttachmentRequest
 * @ProjectName hexo-boot
 * @Description: 附件请求对象
 * @DateTime 2020/9/10 16:10
 */
@Setter
@Getter
public class AttachmentRequest extends BaseRequest<Attachment> {

    private Integer id;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 位置 1：本地 2：七牛云 3：OSS
     */
    private Integer position;

}
