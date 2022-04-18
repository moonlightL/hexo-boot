package com.light.hexo.common.request;

import com.light.hexo.mapper.model.Comment;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author MoonlightL
 * @ClassName: CommentRequest
 * @ProjectName hexo-boot
 * @Description: 评论请求对戏那个
 * @DateTime 2022/1/27, 0027 14:34
 */
@Setter
@Getter
public class CommentRequest extends BaseRequest<Comment> {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 父级 id
     */
    @NotNull(message = "pId不能为空", groups = {CommentRequest.Reply.class})
    private Integer pId;

    /**
     * 页面
     */
    private String page;

    /**
     * 评论内容
     */
    @NotEmpty(message = "评论内容不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class, CommentRequest.Reply.class, CommentRequest.Send.class})
    private String content;

    /**
     * 留言昵称
     */
    @NotEmpty(message = "昵称不能为空", groups = {CommentRequest.Send.class})
    private String nickname;

    /**
     * 邮箱地址
     */
    @NotEmpty(message = "邮箱地址不能为空", groups = {CommentRequest.Send.class})
    private String email;

    /**
     * 主页
     */
    private String homePage;

    /**
     * 头像
     */
    private String avatar;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * 是否删除
     */
    private Boolean delete;

    /**
     * 被回复者昵称
     */
    private String sourceNickname;

    /**
     * 创建时间
     */
    private String createTime;

    public interface Reply {}

    public interface Send {}
}
