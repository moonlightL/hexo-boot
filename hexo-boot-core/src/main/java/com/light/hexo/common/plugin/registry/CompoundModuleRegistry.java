package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.plugin.ModuleRegistry;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

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
        this.addRegister(new MapperRegistry(pluginManager));
        this.addRegister(new ComponentRegistry(pluginManager));
        this.addRegister(new ExtensionRegistry(pluginManager));
        this.addRegister(new HandlerRegistry(pluginManager));
        this.addRegister(new InterceptorRegistry(pluginManager));
        this.addRegister(new ThymeleafRegistry(pluginManager));
    }

    @Override
    public void register(String pluginId) throws Exception {
        for (ModuleRegistry moduleRegistry : this.moduleRegistryList) {
            moduleRegistry.register(pluginId);
        }
    }

    @Override
    public void unRegister(String pluginId) throws Exception {
        for (ModuleRegistry moduleRegistry : this.moduleRegistryList) {
            moduleRegistry.unRegister(pluginId);
        }
    }

    private void addRegister(ModuleRegistry moduleRegistry) {
        this.moduleRegistryList.add(moduleRegistry);
    }
}
