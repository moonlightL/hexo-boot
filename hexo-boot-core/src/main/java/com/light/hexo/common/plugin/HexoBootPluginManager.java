package com.light.hexo.common.plugin;

import com.light.hexo.common.plugin.registry.CompoundModuleRegistry;
import com.light.hexo.common.plugin.rewrite.HexoBootPropertiesPluginDescriptorFinder;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.constant.HexoExceptionEnum;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class HexoBootPluginManager extends BasePluginManager implements InitializingBean {

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

    public PluginState startPlugin(String pluginId, String pluginPath) {
        PluginWrapper plugin = super.getPlugin(pluginId);
        if (plugin == null) {
            super.loadPlugin(Paths.get(pluginPath));
        }

        PluginState pluginState = super.startPlugin(pluginId);
        try {
            this.moduleRegistry.register(pluginId);
        } catch (Exception e) {
            log.error("========== HexoBootPluginManager startPlugin {}===============", e);
            super.unloadPlugin(pluginId);
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_START);
        }

        return pluginState;
    }

    @Override
    public PluginState stopPlugin(String pluginId) {
        PluginWrapper plugin = super.getPlugin(pluginId);
        if (plugin == null) {
            return PluginState.STOPPED;
        }

        PluginState pluginState = super.stopPlugin(pluginId);
        try {
            this.moduleRegistry.unRegister(pluginId);
        } catch (Exception e) {
            log.error("========== HexoBootPluginManager stopPlugin {}===============", e);
        } finally {
            System.gc();
        }

        return pluginState;
    }

    public boolean checkPlugin(String pluginId) {
        boolean result = true;
        try {
            super.checkPluginId(pluginId);
        } catch (Exception e) {
            result = false;
        }
        return result;
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
