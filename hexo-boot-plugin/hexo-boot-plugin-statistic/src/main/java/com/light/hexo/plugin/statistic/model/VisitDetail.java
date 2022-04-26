package com.light.hexo.plugin.statistic.model;

import com.light.hexo.mapper.annotation.CreateTime;
import com.light.hexo.mapper.annotation.UpdateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: VisitDetail
 * @ProjectName hexo-boot
 * @Description: 访问记录详情
 * @DateTime 2022/4/24, 0024 22:42
 */
@Setter
@Getter
@Accessors(chain = true)
@Table(name = "t_ext_visit_detail")
public class VisitDetail  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 客户端 ip
     */
    private String ip;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;
}
