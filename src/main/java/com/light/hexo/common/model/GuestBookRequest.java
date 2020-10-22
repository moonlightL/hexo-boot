package com.light.hexo.common.model;

import com.light.hexo.business.admin.model.GuestBook;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author MoonlightL
 * @ClassName: GuestBookRequest
 * @ProjectName hexo-boot
 * @Description: 留言请求对象
 * @DateTime 2020/9/4 14:58
 */
@Setter
@Getter
public class GuestBookRequest extends BaseRequest<GuestBook> {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 父级 id
     */
    @NotNull(message = "pId不能为空", groups = {GuestBookRequest.Reply.class})
    private Integer pId;

    /**
     * 留言内容
     */
    @NotEmpty(message = "留言内容不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class, GuestBookRequest.Reply.class, GuestBookRequest.Send.class})
    private String content;

    /**
     * 留言昵称
     */
    @NotEmpty(message = "昵称不能为空", groups = {GuestBookRequest.Send.class})
    private String nickname;

    /**
     * 邮箱地址
     */
    @NotEmpty(message = "邮箱地址不能为空", groups = {GuestBookRequest.Send.class})
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * 是否已读
     */
    private Boolean read;

    /**
     * 是否删除
     */
    private Boolean delete;

    /**
     * 创建时间
     */
    private String createTime;

    public interface Reply {}

    public interface Send {}
}
