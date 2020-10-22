package com.light.hexo.common.util;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author MoonlightL
 * @Title: BrowserUtil
 * @ProjectName hexo-boot
 * @Description: 浏览器工具类
 * @Date 2020/8/3 17:33
 */
public class BrowserUtil {

    /**
     * 获取远程客户端浏览器名称
     * @param request
     * @return
     */
    public static String getBrowserName(HttpServletRequest request) {
        Browser browser = getBrowser(request);
        return browser.getName();
    }

    /**
     * 获取远程客户端浏览器版本
     * @param request
     * @return
     */
    public static String getBrowserVersion(HttpServletRequest request) {
        Browser browser = getBrowser(request);
        Version version = browser.getVersion(request.getHeader("User-Agent"));
        return version.getVersion();
    }

    /**
     * 获取远程客户端浏览器信息
     * @param request
     * @return
     */
    public static String getBrowserInfo(HttpServletRequest request) {
        Browser browser = UserAgent.parseUserAgentString(request.getHeader("User-Agent")).getBrowser();
        Version version = browser.getVersion(request.getHeader("User-Agent"));
        return browser.getName() + "/" + version;
    }

    private static Browser getBrowser(HttpServletRequest request) {
        return UserAgent.parseUserAgentString(request.getHeader("User-Agent")).getBrowser();
    }

}
