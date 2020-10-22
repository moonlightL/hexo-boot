package com.light.hexo.common.util;

import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionMap;

/**
 * @Author MoonlightL
 * @ClassName: ExceptionUtil
 * @ProjectName hexo-boot
 * @Description: 异常工具类
 * @DateTime 2020/7/29 17:09
 */
public class ExceptionUtil {

    private ExceptionUtil() {}

    /**
     * 抛异常
     * @param globalExceptionMap
     */
    public static void throwEx(GlobalExceptionMap globalExceptionMap) {
        throw new GlobalException(globalExceptionMap, true);
    }

    /**
     * 抛异常
     * @param code
     * @param message
     */
    public static void throwEx(int code, String message) {
        throw new GlobalException(code, message);
    }

    /**
     * 抛异常到异常页面
     * @param globalExceptionMap
     */
    public static void throwExToPage(GlobalExceptionMap globalExceptionMap) {
        throw new GlobalException(globalExceptionMap, false);
    }

    /**
     * 抛异常，转向 html 页面
     * @param message
     */
    public static void throwEx(String message) {
        throw new GlobalException(message);
    }


}
