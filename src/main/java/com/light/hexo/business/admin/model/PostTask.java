package com.light.hexo.business.admin.model;

import com.light.hexo.common.component.mybatis.CreateTime;
import com.light.hexo.common.component.mybatis.UpdateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: PostTask
 * @ProjectName hexo-boot
 * @Description: 文章任务
 * @DateTime 2021/7/12 9:44
 */
@Setter
@Getter
@Accessors(chain = true)
@ToString
@Table(name = "t_post_task")
public class PostTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 文章 id
     */
    private Integer postId;

    /**
     * 状态  0：待执行 1：已执行
     */
    private Integer state;

    /**
     * 发表时间
     */
    private LocalDateTime jobTime;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;
}
