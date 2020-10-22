package com.light.hexo.business.admin.model;

import com.light.hexo.common.component.mybatis.CreateTime;
import com.light.hexo.common.component.mybatis.UpdateTime;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: FriendLink
 * @ProjectName hexo-boot
 * @Description: 友链
 * @DateTime 2020/9/22 17:42
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_friend_link")
public class FriendLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 主页
     */
    private String homeUrl;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 类型 1：博主主页 2：常用网址
     */
    private Integer linkType;

    /**
     * 背景颜色
     */
    private String backgroundColor;

    /**
     * 备注
     */
    private String remark;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;
}
