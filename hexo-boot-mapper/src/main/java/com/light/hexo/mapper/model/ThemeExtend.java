package com.light.hexo.mapper.model;

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
     * 配置标签
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

    /**
     * 归类 1：基础信息 2：页面配置 3：布局设置 4：评论设置 5：资源设置
     */
    private Integer tab;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;
}
