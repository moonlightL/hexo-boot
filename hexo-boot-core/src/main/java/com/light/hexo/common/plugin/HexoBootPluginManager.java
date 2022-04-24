package com.light.hexo.common.plugin;

import com.light.hexo.common.plugin.registry.CompoundModuleRegistry;
import com.light.hexo.common.plugin.rewrite.HexoBootPropertiesPluginDescriptorFinder;
import lombok.SneakyThrows;
import org.pf4j.*;
import org.springframework.beans.factory.InitializingBean;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @Override
    protected PluginDescriptorFinder createPluginDescriptorFinder() {
        return new CompoundPluginDescriptorFinder()
                .add(new HexoBootPropertiesPluginDescriptorFinder())
                .add(new ManifestPluginDescriptorFinder());
    }


    @SneakyThrows
    public PluginState startPlugin(String pluginId, String pluginPath) {
        PluginWrapper plugin = super.getPlugin(pluginId);
        if (plugin == null) {
            super.loadPlugin(Paths.get(pluginPath));
        }

        PluginState pluginState = super.startPlugin(pluginId);
        this.moduleRegistry.register(pluginId);

        return pluginState;
    }

    @SneakyThrows
    @Override
    public PluginState stopPlugin(String pluginId) {
        PluginWrapper plugin = super.getPlugin(pluginId);
        if (plugin == null) {
            return PluginState.STOPPED;
        }

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

    public ModuleRegistry getModuleRegistry() {
        return this.moduleRegistry;
    }
}
