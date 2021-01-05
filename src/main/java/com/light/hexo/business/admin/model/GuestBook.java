package com.light.hexo.business.admin.model;

import com.light.hexo.common.component.mybatis.CreateTime;
import com.light.hexo.common.component.mybatis.UpdateTime;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: Guestbook
 * @ProjectName hexo-boot
 * @Description: 留言板
 * @DateTime 2020/7/29 17:29
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_guest_book")
public class GuestBook implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 留言用户 id
     */
    private Integer userId;

    /**
     * 留言昵称
     */
    private String nickname;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 父级留言 id
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
    private GuestBook parent;

    @Transient
    private List<GuestBook> replyList;

    /**
     * ip 是否加入黑名单
     */
    @Transient
    private Boolean blacklist;

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
    private String avatar;

    /**
     * 是否为博主
     */
    @Column(name = "is_blogger")
    private Boolean blogger;
}
