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
 * @ClassName: Config
 * @ProjectName hexo-boot
 * @Description: 全局配置
 * @DateTime 2020/9/9 11:11
 */
@Setter
@Getter
@Accessors(chain = true)
@ToString
@Table(name = "t_config")
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 参数名
     */
    private String configKey;

    /**
     * 参数值
     */
    private String configValue;

    /**
     * 备注
     */
    private String remark;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;
}
