package com.light.hexo.mapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author MoonlightL
 * @ClassName: UpdateTime
 * @ProjectName hexo-boot
 * @Description: 修改时间注解
 * @DateTime 2020/7/29 17:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface UpdateTime {
}
