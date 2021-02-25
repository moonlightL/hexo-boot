package com.light.hexo.business.admin.model.extend;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: ThemeFile
 * @ProjectName hexo-boot
 * @Description: theme.json 封装对象
 * @DateTime 2021/2/7 17:39
 */
@Setter
@Getter
@ToString
public class ThemeFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 扩展
     */
    private List<ThemeFileExtension> extension;
}
