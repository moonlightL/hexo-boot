package com.light.hexo.common.request;

import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.mapper.model.SysPlugin;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author MoonlightL
 * @ClassName: PluginRequest
 * @ProjectName hexo-boot
 * @Description: 插件请求对象
 * @DateTime 2022/4/13, 0013 10:26
 */
@Setter
@Getter
public class PluginRequest extends BaseRequest<SysPlugin> {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {BaseRequest.Update.class})
    private Integer id;

    /**
     * 插件名称
     */
    @NotEmpty(message = "插件名称不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private String name;

    /**
     * 可用状态
     */
    private Boolean state;

    /**
     * 描述
     */
    private String remark;

    /**
     * 版本号
     */
    private String version;
}
