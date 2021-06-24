package com.light.hexo.business.admin.model;

import com.light.hexo.common.component.mybatis.CreateTime;
import com.light.hexo.common.component.mybatis.UpdateTime;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: Dynamic
 * @ProjectName hexo-boot
 * @Description: 动态
 * @DateTime 2021/6/23 14:54
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_dynamic")
public class Dynamic implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 内容
     */
    private String content;

    /**
     * 颜色
     */
    private String color;

    /**
     * 点赞数
     */
    private Integer praiseNum;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;

    /**
     * 时间描述
     */
    @Transient
    private String timeDesc;
}
