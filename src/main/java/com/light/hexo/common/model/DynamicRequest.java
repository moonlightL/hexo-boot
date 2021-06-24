package com.light.hexo.common.model;

import com.light.hexo.business.admin.model.Dynamic;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @Author MoonlightL
 * @ClassName: DynamicRequest
 * @ProjectName hexo-boot
 * @Description: 动态请求对象
 * @DateTime 2021/6/23 15:42
 */
@Setter
@Getter
public class DynamicRequest extends BaseRequest<Dynamic> {

    private Integer id;

    /**
     * 内容
     */
    @NotEmpty(message = "动态内容不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private String content;

    /**
     * 颜色
     */
    private String color;

    /**
     * 点赞数
     */
    private Integer praiseNum;


    public interface Send {}
}
