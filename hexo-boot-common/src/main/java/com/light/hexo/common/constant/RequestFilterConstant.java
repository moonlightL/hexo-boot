package com.light.hexo.common.constant;

/**
 * @Author: MoonlightL
 * @ClassName: RequestFilterConstant
 * @ProjectName: hexo-boot
 * @Description: 请求过滤常量
 * @DateTime: 2022-05-01 09:43
 */
public final class RequestFilterConstant {

    /**
     * 过滤指定开头 URL
     */
    public static final String[] FILTER_START_URL = {"/admin", "/plugin"};

    /**
     * 过滤执行结尾 URL
     */
    public static final String[] FILTER_END_URL = {".json", ".css", ".js", ".map", ".ico", ".jpg", ".png", ".mp3", ".mp4"};

    /**
     * 机器人/爬虫来源
     */
    public static final String ROBOT_SOURCE = "Robot/Spider";

    /**
     * 访问用户 id
     */
    public static final String VISIT_COOKIE_NAME = "V_UNIQUE_ID";

}
