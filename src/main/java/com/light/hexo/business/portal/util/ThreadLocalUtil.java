package com.light.hexo.business.portal.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: ThreadLocalUtil
 * @ProjectName hexo-boot
 * @Description: 线程工具类
 * @DateTime 2020/9/21 16:54
 */
public class ThreadLocalUtil {

    private static final ThreadLocal<Map<String, Object>> CONTEXT_MAP = new ThreadLocal<>();

    public static void set(String key, Object object) {
        Map<String, Object> dataMap = CONTEXT_MAP.get();
        if (dataMap == null) {
            dataMap = new HashMap<>();
            CONTEXT_MAP.set(dataMap);
        }
        dataMap.put(key, object);
    }

    public static Object get(String key) {
        Map<String, Object> dataMap = CONTEXT_MAP.get();
        return dataMap != null ? dataMap.get(key) : null;
    }

    public static void remove(String key) {
        Map<String, Object> map = CONTEXT_MAP.get();
        map.remove(key);
    }

    public static void clear() {
        CONTEXT_MAP.remove();
    }

    private ThreadLocalUtil() {}

}
