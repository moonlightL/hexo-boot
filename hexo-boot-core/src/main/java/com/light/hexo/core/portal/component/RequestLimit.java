package com.light.hexo.core.portal.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author MoonlightL
 * @ClassName: RequestLimit
 * @ProjectName hexo-boot
 * @Description: 请求限制注解
 * @DateTime 2020/9/21 16:25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {

    /**
     * 缓存 name
     * @return
     */
    String cacheName();

    /**
     * 限制时间
     * @return
     */
    int time() default 30;

    /**
     * 提示
     * @return
     */
    String msg() default "评论过于频繁，请等待30秒后再评论";
}
