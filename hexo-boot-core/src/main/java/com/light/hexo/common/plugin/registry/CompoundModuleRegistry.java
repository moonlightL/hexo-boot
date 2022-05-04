package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.plugin.ModuleRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: CompoundModuleRegistry
 * @ProjectName hexo-boot
 * @Description: 组合模块注册器
 * @DateTime 2022/4/20, 0020 10:54
 */
public class CompoundModuleRegistry extends AbstractModuleRegistry implements ModuleRegistry {

    private List<ModuleRegistry> moduleRegistryList;

    public CompoundModuleRegistry(HexoBootPluginManager pluginManager) {
        super(pluginManager);
        this.moduleRegistryList = Collections.synchronizedList(new ArrayList<>());
        this.moduleRegistryList.add(new TkMapperRegistry(pluginManager));
        this.moduleRegistryList.add(new ComponentRegistry(pluginManager));
        this.moduleRegistryList.add(new ExtensionRegistry(pluginManager));
        this.moduleRegistryList.add(new HandlerRegistry(pluginManager));
        this.moduleRegistryList.add(new InterceptorRegistry(pluginManager));
        this.moduleRegistryList.add(new ThymeleafRegistry(pluginManager));
    }

    @Override
    public void register(String pluginId) {
        for (ModuleRegistry moduleRegistry : this.moduleRegistryList) {
            moduleRegistry.register(pluginId);
        }
    }

    @Override
    public void unRegister(String pluginId) {
        for (ModuleRegistry moduleRegistry : this.moduleRegistryList) {
            moduleRegistry.unRegister(pluginId);
        }
        super.clearPluginClass(pluginId);
    }
}
