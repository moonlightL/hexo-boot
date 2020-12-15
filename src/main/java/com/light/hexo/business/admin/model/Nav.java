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
 * @ClassName: Nav
 * @ProjectName hexo-boot
 * @Description: 导航
 * @DateTime 2020/12/14 17:27
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_nav")
public class Nav implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 链接
     */
    private String link;

    /**
     * 编码
     */
    private String code;

    /**
     * 图标
     */
    private String icon;

    /**
     * 类型 1：默认 2：自定义
     */
    private Integer navType;

    /**
     * 可用状态
     */
    private Boolean state;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 内容
     */
    private String content;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;
}
