package com.light.hexo.mapper.model;

import com.light.hexo.mapper.annotation.CreateTime;
import com.light.hexo.mapper.annotation.UpdateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: Comment
 * @ProjectName hexo-boot
 * @Description: 评论
 * @DateTime 2022/1/27, 0027 13:39
 */
@Setter
@Getter
@Accessors(chain = true)
@ToString
@Table(name = "t_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 评论页面
     */
    private String page;

    /**
     * 留言昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 主页
     */
    private String homePage;

    /**
     * 是否为博主
     */
    @Column(name = "is_blogger")
    private Boolean blogger;

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
     * 系统名称
     */
    private String osName;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 点赞数
     */
    private Integer praiseNum;

    /**
     * 评论类型 1:文章  2:留言
     */
    private Integer commentType;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;

    /**
     * 原文内容
     */
    @Transient
    private Comment parent;

    /**
     * 回复列表
     */
    @Transient
    private List<Comment> replyList;

    /**
     * ip 是否加入黑名单
     */
    @Transient
    private Boolean blacklist;

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

    public String getDate() {
        return this.createTime.toLocalDate().toString();
    }

}

