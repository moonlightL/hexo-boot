package com.light.hexo.common.model;

import com.light.hexo.business.admin.model.Tag;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author MoonlightL
 * @ClassName: TagRequest
 * @ProjectName hexo-boot
 * @Description: 标签请求对象
 * @DateTime 2020/8/11 17:33
 */
@Setter
@Getter
public class TagRequest extends BaseRequest<Tag> {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {BaseRequest.Update.class})
    private Integer id;

    /**
     * 标签名
     */
    @NotEmpty(message = "标签名称不能为空", groups = {BaseRequest.Update.class})
    private String name;
}
