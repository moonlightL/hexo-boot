package com.light.hexo.common.request;

import com.light.hexo.mapper.model.Nav;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author MoonlightL
 * @ClassName: NavRequest
 * @ProjectName hexo-boot
 * @Description: 导航请求对戏那个
 * @DateTime 2020/12/14 18:17
 */
@Setter
@Getter
public class NavRequest extends BaseRequest<Nav> {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {BaseRequest.Update.class})
    private Integer id;

    /**
     * 导航名称
     */
    @NotEmpty(message = "导航名称不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private String name;

    /**
     * 链接
     */
    @NotEmpty(message = "导航链接不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private String link;

    /**
     * 编码
     */
    @NotEmpty(message = "导航编码不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private String code;

    /**
     * 图标
     */
    private String icon;

    /**
     * 状态 1：可用 0：禁用
     */
    private Boolean state;

    /**
     * 导航页面内容
     */
    private String content;

    /**
     * 排序
     */
    private String sort;

    /**
     * 父级 id
     */
    private Integer parentId;
    
    
    /**
     * 封面
     */
    private String cover;
}
