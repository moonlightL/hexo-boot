package com.light.hexo.common.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author MoonlightL
 * @ClassName: InterceptPathPattern
 * @ProjectName hexo-boot
 * @Description: 插件拦截器注解
 * @DateTime 2022/4/20, 0020 10:36
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InterceptPathPattern {

    String[] value();
}
