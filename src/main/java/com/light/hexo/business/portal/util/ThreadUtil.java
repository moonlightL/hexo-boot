package com.light.hexo.business.portal.util;

import com.light.hexo.business.admin.model.User;

/**
 * @Author MoonlightL
 * @ClassName: ThreadUtil
 * @ProjectName hexo-boot
 * @Description: 线程工具类
 * @DateTime 2020/9/21 16:54
 */
public class ThreadUtil {

    private static final ThreadLocal<User> map = new ThreadLocal<>();

    public static void set(User t) {
        map.set(t);
    }

    public static User get() {
        return map.get();
    }

    public static void remove() {
        map.remove();
    }

    private ThreadUtil() {}

}
