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
 * @ClassName: Category
 * @ProjectName hexo-boot
 * @Description: 分类
 * @DateTime 2020/7/29 17:27
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 图片地址
     */
    private String coverUrl;

    /**
     * 可用状态
     */
    private Boolean state;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 描述
     */
    private String remark;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;

    /**
     * 文章数
     */
    @Transient
    private Integer postNum;
}
