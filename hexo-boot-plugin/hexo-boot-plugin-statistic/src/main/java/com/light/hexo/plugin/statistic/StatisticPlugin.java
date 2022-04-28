package com.light.hexo.plugin.statistic;

import com.light.hexo.common.plugin.BasePluginManager;
import com.light.hexo.common.plugin.BasePlugin;
import org.pf4j.PluginWrapper;

/**
 * @Author MoonlightL
 * @ClassName: StatisticPlugin
 * @ProjectName hexo-boot
 * @Description: 生命周期
 * @DateTime 2022/4/24, 0024 16:35
 */
public class StatisticPlugin extends BasePlugin {

    public StatisticPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public String getConfigUrl() {
        return "/plugin/statistic/configUI.html";
    }

    @Override
    public void start() {
        super.start();

        PluginWrapper pluginWrapper = super.getWrapper();
        ClassLoader pluginClassLoader = pluginWrapper.getPluginClassLoader();
        BasePluginManager pluginManager = (BasePluginManager) super.getWrapper().getPluginManager();
        pluginManager.runSqlFile(pluginClassLoader, "sql/statistic.sql");
    }
}
