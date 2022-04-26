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
 * @ClassName: SysPlugin
 * @ProjectName hexo-boot
 * @Description: 插件
 * @DateTime 2022/4/13, 0013 10:20
 */
@Setter
@Getter
@Accessors(chain = true)
@ToString
@Table(name = "t_sys_plugin")
public class SysPlugin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 插件唯一标识
     */
    private String pluginId;

    /**
     * 名称
     */
    private String originName;

    /**
     * 状态
     */
    private Boolean state;

    /**
     * 备注
     */
    private String remark;

    /**
     * 版本号
     */
    private String version;

    /**
     * 作者
     */
    private String author;

    /**
     * 本地路径
     */
    private String filePath;

    /**
     * 配置页面
     */
    private String configUrl;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;
}
