package com.light.hexo.common.request;

import com.light.hexo.mapper.model.Theme;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @Author MoonlightL
 * @ClassName: ThemeRequest
 * @ProjectName hexo-boot
 * @Description: 主题请求对象
 * @DateTime 2020/9/24 14:40
 */
@Setter
@Getter
public class ThemeRequest extends BaseRequest<Theme> {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {BaseRequest.Update.class})
    private Integer id;

    /**
     * 主题名称
     */
    private String name;

    /**
     * 状态
     */
    private Boolean state;
}
