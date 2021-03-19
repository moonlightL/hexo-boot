package com.light.hexo.business.admin.model;

import com.light.hexo.common.component.mybatis.CreateTime;
import com.light.hexo.common.component.mybatis.UpdateTime;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: Post
 * @ProjectName hexo-boot
 * @Description: 文章
 * @DateTime 2020/7/29 17:27
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_post")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 摘要（html 格式）
     */
    private String summaryHtml;

    /**
     * 文章内容（markdown 格式）
     */
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
     * 发布年份
     */
    private String year;

    /**
     * 发布月份
     */
    private String month;

    /**
     * 发布天
     */
    private String day;

    /**
     * 封面图片地址
     */
    private String coverUrl;

    /**
     * 是否发布
     */
    @Column(name="is_publish")
    private Boolean publish;

    /**
     * 是否允许评论
     */
    @Column(name="is_comment")
    private Boolean comment;

    /**
     * 是否置顶
     */
    @Column(name = "is_top")
    private Boolean top;

    /**
     * 是否删除
     */
    @Column(name = "is_delete")
    private Boolean delete;

    /**
     * 是否转载
     */
    @Column(name = "is_reprint")
    private Boolean reprint;

    /**
     * 转载链接
     */
    private String reprintLink;

    /**
     * 分类
     */
    private Integer categoryId;

    /**
     * 分类名称
     */
    @Transient
    private String categoryName;

    /**
     * 标签，多个标签使用逗号拼接
     */
    private String tags;

    /**
     * 文章链接
     */
    private String link;

    /**
     * 浏览数
     */
    private Integer readNum;

    /**
     * 点赞数
     */
    private Integer praiseNum;

    /**
     * 评论数
     */
    private Integer commentNum;

    /**
     * 置顶时间
     */
    private LocalDateTime topTime;

    @Transient
    private Post prevPost;

    @Transient
    private Post nextPost;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;
}
