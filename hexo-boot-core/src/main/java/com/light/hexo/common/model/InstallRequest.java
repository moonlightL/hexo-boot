package com.light.hexo.common.model;

import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @Author MoonlightL
 * @ClassName: InstallRequest
 * @ProjectName hexo-boot
 * @Description: 安装请求对象
 * @DateTime 2020/9/24 16:20
 */
@Setter
@Getter
public class InstallRequest extends BaseRequest<Object> {

    /**
     * 用户名
     */
    @NotEmpty(message = "用户名不能为空", groups = {BaseRequest.Save.class})
    private String username;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空", groups = {BaseRequest.Save.class})
    private String password;

    /**
     * 博客名称
     */
    private String blogName;

    /**
     * 博客首页
     */
    private String homePage;

    /**
     * 博客描述
     */
    private String description;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱地址
     */
    private String email;

}
