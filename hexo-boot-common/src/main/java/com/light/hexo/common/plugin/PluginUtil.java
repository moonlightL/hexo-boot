package com.light.hexo.common.plugin;

import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: PluginUtil
 * @ProjectName hexo-boot
 * @Description: 插件工具类
 * @DateTime 2022/4/19, 0019 20:49
 */
public class PluginUtil {

    static final Map<String, ApplicationContext> map = Collections.synchronizedMap(new HashMap<>());

    public static final void put(String pluginId, ApplicationContext applicationContext) {
        map.putIfAbsent(pluginId, applicationContext);
    }

    public static final void remove(String pluginId) {
        map.remove(pluginId);
    }

    public static final ApplicationContext get(String pluginId) {
        return map.get(pluginId);
    }

    public static final Object getBean(String pluginId, Class<?> clazz) {
        return get(pluginId).getBean(clazz);
    }
}
