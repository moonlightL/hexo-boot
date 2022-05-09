package com.light.hexo.common.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author MoonlightL
 * @ClassName: HexoBootTask
 * @ProjectName hexo-boot
 * @Description: 插件定时器注解
 * @DateTime 2022/4/24, 0024 16:51
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public  @interface HexoBootTask {
}
