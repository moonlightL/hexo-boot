package com.light.hexo.plugin.statistic.model;

import com.light.hexo.mapper.annotation.CreateTime;
import com.light.hexo.mapper.annotation.UpdateTime;
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
 * @ClassName: VisitInfo
 * @ProjectName hexo-boot
 * @Description: 访问记录信息
 * @DateTime 2022/4/24, 0024 16:43
 */
@Setter
@Getter
@Accessors(chain = true)
@Table(name = "t_ext_visit_info")
public class VisitInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 页面访问量
     */
    private Integer pv;

    /**
     * 用户访问量
     */
    private Integer uv;

    /**
     * 当天日期，格式 yyyyMMdd
     */
    private Integer period;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;

}
