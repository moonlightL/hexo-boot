package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;

/**
 * @Author MoonlightL
 * @ClassName: ExtensionRegistry
 * @ProjectName hexo-boot
 * @Description: 扩展注册器
 * @DateTime 2022/4/20, 0020 10:13
 */
public class ExtensionRegistry extends AbstractModuleRegistry {

    public ExtensionRegistry(HexoBootPluginManager pluginManager) {
        super(pluginManager);
    }

    @Override
    public void register(String pluginId) throws Exception {
        for (String extensionClassName : super.pluginManager.getExtensionClassNames(pluginId)) {
            try {
                Class<?> extensionClass = super.pluginManager.getPlugin(pluginId).getPluginClassLoader().loadClass(extensionClassName);
                super.registryBean(extensionClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unRegister(String pluginId) throws Exception {
        for (String extensionClassName : super.pluginManager.getExtensionClassNames(pluginId)) {
            try {
                Class<?> extensionClass = super.pluginManager.getPlugin(pluginId).getPluginClassLoader().loadClass(extensionClassName);
                super.destroyBean(extensionClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
