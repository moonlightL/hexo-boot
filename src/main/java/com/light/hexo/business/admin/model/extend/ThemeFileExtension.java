package com.light.hexo.business.admin.model.extend;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author MoonlightL
 * @ClassName: ThemeFileExtension
 * @ProjectName hexo-boot
 * @Description: theme.json 封装对象
 * @DateTime 2021/2/7 17:41
 */
@Setter
@Getter
@ToString
public class ThemeFileExtension implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 属性名
     */
    private String key;

    /**
     * 属性值
     */
    private String value;

    /**
     * 属性类型（input,select）
     */
    private String type;

    /**
     * 当 type = select 时才有值，多个值使用英文逗号拼接
     */
    private String option;

    /**
     * 标签，用于页面展示
     */
    private String label;

    /**
     * 启动项目后是否同步到数据库
     */
    private boolean update;
}
