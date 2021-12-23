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
 * @ClassName: ThemeExtend
 * @ProjectName hexo-boot
 * @Description: 主题配置扩展
 * @DateTime 2020/10/27 16:59
 */
@Setter
@Getter
@Accessors(chain = true)
@ToString
@Table(name = "t_theme_extend")
public class ThemeExtend implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 主题 id
     */
    private Integer themeId;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 标签
     */
    private String configLabel;

    /**
     * 配置类型（如：input，select）
     */
    private String configType;

    /**
     * 选项
     */
    private String configOption;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;
}
