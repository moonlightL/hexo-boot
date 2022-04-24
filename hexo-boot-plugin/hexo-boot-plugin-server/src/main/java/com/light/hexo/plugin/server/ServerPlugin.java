package com.light.hexo.plugin.server;

import com.light.hexo.common.plugin.BasePlugin;
import org.pf4j.PluginWrapper;

/**
 * @Author MoonlightL
 * @ClassName: ServerPlugin
 * @ProjectName hexo-boot
 * @Description: 生命周期
 * @DateTime 2022/4/12, 0012 18:30
 */
public class ServerPlugin extends BasePlugin {

    public ServerPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public String getConfigUrl() {
        return "/plugin/server/configUI.html";
    }
}
