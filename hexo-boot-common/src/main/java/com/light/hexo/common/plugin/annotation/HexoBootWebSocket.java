package com.light.hexo.common.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author MoonlightL
 * @ClassName: HexoBootWebSocket
 * @ProjectName hexo-boot
 * @Description: WebSocket 注解
 * @DateTime 2022/5/19, 0019 11:04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HexoBootWebSocket {

    String url();

    Class handlerClass();

    Class interceptorClass() default Void.class;
}
