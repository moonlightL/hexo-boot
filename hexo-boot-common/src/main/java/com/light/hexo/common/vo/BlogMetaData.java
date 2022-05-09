package com.light.hexo.common.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author MoonlightL
 * @ClassName: BlogMetaData
 * @ProjectName hexo-boot
 * @Description: 博客元信息
 * @DateTime 2022/4/23, 0023 15:29
 */
@Setter
@Getter
public class BlogMetaData  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String homeDir;

    private String attachmentDir;

    private String logDir;

    private String pluginDir;

    private String version;
}
