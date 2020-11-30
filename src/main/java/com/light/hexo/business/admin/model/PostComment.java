package com.light.hexo.business.admin.model;

import com.light.hexo.common.component.mybatis.CreateTime;
import com.light.hexo.common.component.mybatis.UpdateTime;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: PostComment
 * @ProjectName hexo-boot
 * @Description: 文章评论
 * @DateTime 2020/8/21 11:47
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_post_comment")
public class PostComment  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

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
     * 留言昵称
     */
    private String nickname;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父级评论 id
     */
    private Integer pId;

    /**
     * 面板 id
     */
    private Integer bannerId;

    /**
     * 被回复者昵称
     */
    private String sourceNickname;

    /**
     * 是否删除
     */
    @Column(name = "is_delete")
    private Boolean delete;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * 浏览器
     */
    private String browser;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;

    /**
     * 原文内容
     */
    @Transient
    private PostComment parent;

    /**
     * 回复列表
     */
    @Transient
    private List<PostComment> replyList;

    /**
     * ip 是否加入黑名单
     */
    @Transient
    private Boolean blacklist;

    /**
     * 文章链接
     */
    @Transient
    private String postLink;

    /**
     * 邮箱地址
     */
    @Transient
    private String email;

    /**
     * ip 信息
     */
    @Transient
    private String ipInfo;

    /**
     * 时间描述
     */
    @Transient
    private String timeDesc;

    /**
     * 头像
     */
    @Transient
    private String avatar;

    /**
     * 是否为博主
     */
    @Transient
    private Boolean blogger;
}
