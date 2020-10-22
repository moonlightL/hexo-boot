package com.light.hexo.common.model;

import com.light.hexo.business.admin.model.PostComment;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author MoonlightL
 * @ClassName: PostCommentRequest
 * @ProjectName hexo-boot
 * @Description: 文章评论请求对象
 * @DateTime 2020/8/21 11:54
 */
@Setter
@Getter
public class PostCommentRequest extends BaseRequest<PostComment> {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 父级 id
     */
    @NotNull(message = "pId不能为空", groups = {PostCommentRequest.Reply.class})
    private Integer pId;

    /**
     * 文章 id
     */
    private Integer postId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 评论用户 id
     */
    private Integer userId;

    /**
     * 评论内容
     */
    @NotEmpty(message = "评论内容不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class, PostCommentRequest.Reply.class, PostCommentRequest.Send.class})
    private String content;

    /**
     * 留言昵称
     */
    @NotEmpty(message = "昵称不能为空", groups = {PostCommentRequest.Send.class})
    private String nickname;

    /**
     * 邮箱地址
     */
    @NotEmpty(message = "邮箱地址不能为空", groups = {PostCommentRequest.Send.class})
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
