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
import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: Theme
 * @ProjectName hexo-boot
 * @Description: 主题
 * @DateTime 2020/9/24 14:34
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_theme")
public class Theme implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 主题名称
     */
    private String name;

    /**
     * 预览图片地址（800x500 PNG ）
     */
    private String coverUrl;

    /**
     * 状态
     */
    private Boolean state;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 版本号
     */
    private String version;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;

    @Transient
    private Map<String, String> configMap;
}
