package com.light.hexo.common.util;

import com.light.hexo.common.plugin.BasePlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: PluginUtil
 * @ProjectName hexo-boot
 * @Description: 插件工具
 * @DateTime 2022/11/16, 0016 16:33
 */
public class PluginUtil {

    public static final Map<String, BasePlugin> PLUGIN_MAP = new HashMap<>();

    public static void put(String name, BasePlugin plugin) {
        PLUGIN_MAP.put(name, plugin);
    }

    public static BasePlugin get(String name) {
        return PLUGIN_MAP.get(name);
    }

    public static void remove(String name) {
        PLUGIN_MAP.remove(name);
    }
}
