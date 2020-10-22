package com.light.hexo.common.model;

import com.light.hexo.business.admin.model.Category;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.valid.NumberValidator;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author MoonlightL
 * @ClassName: CategoryRequest
 * @ProjectName hexo-boot
 * @Description: 分类请求对象
 * @DateTime 2020/8/11 19:26
 */
@Setter
@Getter
public class CategoryRequest extends BaseRequest<Category> {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {BaseRequest.Update.class})
    private Integer id;
    /**
     * 分类名称
     */
    @NotEmpty(message = "分类名称不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private String name;

    /**
     * 图片地址
     */
    private String coverUrl;

    /**
     * 可用状态
     */
    private Boolean state;

    /**
     * 排序
     */
    @NumberValidator(message= "排序必须为数字", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private String sort;

    /**
     * 描述
     */
    private String remark;
}
