package com.light.hexo.common.plugin;

import com.light.hexo.common.plugin.registry.CompoundModuleRegistry;
import lombok.SneakyThrows;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginState;
import org.springframework.beans.factory.InitializingBean;

import java.nio.file.Path;

/**
 * @Author MoonlightL
 * @ClassName: HexoBootPluginManager
 * @ProjectName hexo-boot
 * @Description: 插件管理器
 * @DateTime 2022/4/19, 0019 17:37
 */
public class HexoBootPluginManager extends AbstractPluginManager implements InitializingBean {

    private ModuleRegistry moduleRegistry;

    public HexoBootPluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    @SneakyThrows
    @Override
    public PluginState startPlugin(String pluginId) {
        PluginState pluginState = super.startPlugin(pluginId);
        this.moduleRegistry.register(pluginId);
        return pluginState;
    }

    @SneakyThrows
    @Override
    public PluginState stopPlugin(String pluginId) {
        PluginState pluginState = super.stopPlugin(pluginId);
        this.moduleRegistry.unRegister(pluginId);
        return pluginState;
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new HexoBootSpringExtensionFactory(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.moduleRegistry = new CompoundModuleRegistry(this);
    }
}
