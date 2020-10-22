package com.light.hexo.common.constant;

/**
 * @Author MoonlightL
 * @ClassName: CacheKey
 * @ProjectName hexo-boot
 * @Description: 搭配 CacheUtil 使用
 * @DateTime 2020/9/9 15:43
 */
public final class CacheKey {

    /**
     * 黑名单列表
     */
    public static final String BLACK_LIST = "hexo:black:list";

    /**
     * 全局配置列表
     */
    public static final String CONFIG_LIST = "hexo:config:list";

    /**
     * 友链列表
     */
    public static final String FRIEND_LINK_LIST = "hexo:friend:link:list";

    /**
     * 当前使用的主题
     */
    public static final String CURRENT_THEME = "hexo:current:theme";

    /**
     * 登录失败次数
     */
    public static final String LOGIN_ERROR_NUM = "hexo:login:error:num";
}
