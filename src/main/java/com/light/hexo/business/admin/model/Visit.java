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
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: Visit
 * @ProjectName hexo-boot
 * @Description: 网站访问表
 * @DateTime 2020/9/16 13:39
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_visit")
public class Visit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 访问日期
     */
    private LocalDate visitDate;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;
}
