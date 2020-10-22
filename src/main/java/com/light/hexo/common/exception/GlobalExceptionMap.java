package com.light.hexo.common.exception;

/**
 * @Author MoonlightL
 * @ClassName: GlobalExceptionMap
 * @ProjectName hexo-boot
 * @Description: 异常枚举接口
 * @DateTime 2020/7/29 17:06
 */
public interface GlobalExceptionMap {

    /**
     * 返回 code
     * @return
     */
    int getCode();

    /**
     * 返回消息
     * @return
     */
    String getMessage();
}
