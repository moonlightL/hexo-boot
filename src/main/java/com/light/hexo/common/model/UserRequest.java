package com.light.hexo.common.model;

import com.light.hexo.business.admin.model.User;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author MoonlightL
 * @ClassName: UserRequest
 * @ProjectName hexo-boot
 * @Description: 用户请求对象
 * @DateTime 2020/9/7 18:02
 */
@Setter
@Getter
public class UserRequest extends BaseRequest<User> {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {BaseRequest.Update.class, UserRequest.UpdatePwd.class})
    private Integer id;

    /**
     * 用户名
     */
    @NotEmpty(message = "用户名不能为空", groups = {BaseRequest.Save.class, UserRequest.Login.class})
    private String username;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空", groups = {BaseRequest.Save.class, UserRequest.UpdatePwd.class, UserRequest.Login.class})
    private String password;

    /**
     * 验证码
     */
    @NotEmpty(message = "验证码不能为空", groups = {UserRequest.Login.class})
    private String verifyCode;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 可用状态
     */
    private Boolean state;

    /**
     * 角色 1：博主 2：用户
     */
    private Integer role;

    @NotEmpty(message = "旧密码不能为空", groups = {UserRequest.UpdatePwd.class})
    private String oldPassword;

    public interface Login {}

    public interface UpdatePwd {}
}
