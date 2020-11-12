package com.light.hexo.common.model;

import com.light.hexo.business.admin.model.Post;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author MoonlightL
 * @ClassName: PostRequest
 * @ProjectName hexo-boot
 * @Description: 文章请求对象
 * @DateTime 2020/8/11 21:00
 */
@Setter
@Getter
public class PostRequest extends BaseRequest<Post> {

    @NotNull(message = "主键不能为空", groups = {BaseRequest.Update.class})
    private Integer id;

    /**
     * 文章标题
     */
    @NotEmpty(message = "文章标题不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private String title;

    /**
     * 文章内容（markdown 格式）
     */
    @NotEmpty(message = "文章内容不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private String content;

    /**
     * 作者
     */
    private String author;

    /**
     * 发布日期
     */
    private String publishDate;

    /**
     * 封面图片地址
     */
    private String coverUrl;

    /**
     * 是否发布
     */
    private Boolean publish;

    /**
     * 是否允许评论
     */
    private Boolean comment;

    /**
     * 是否置顶
     */
    private Boolean top;

    /**
     * 是否转载
     */
    private Boolean reprint;

    /**
     * 转载链接
     */
    private String reprintLink;

    /**
     * 分类
     */
    @NotNull(message = "分类不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private Integer categoryId;

    /**
     * 标签，多个标签使用逗号拼接
     */
    private String tags;
}
