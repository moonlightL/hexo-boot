package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: ComponentRegistry
 * @ProjectName hexo-boot
 * @Description: 组件注册器
 * @DateTime 2022/4/20, 0020 10:08
 */
public class ComponentRegistry extends AbstractModuleRegistry {

    public ComponentRegistry(HexoBootPluginManager pluginManager) {
        super(pluginManager);
    }

    @SneakyThrows
    @Override
    public void register(String pluginId) {
        for (Class<?> clazz : this.listComponentClasses(pluginId)) {
            super.registryBean(clazz);
        }
    }

    @SneakyThrows
    @Override
    public void unRegister(String pluginId) {
        for (Class<?> clazz : this.listComponentClasses(pluginId)) {
            super.destroyBean(clazz);
        }
    }

    private List<Class<?>> listComponentClasses(String pluginId) throws Exception {
        List<Class<?>> classList = new ArrayList<>();

        for (Class<?> pluginClass : super.getPluginClasses(pluginId)) {
            Component annotation = pluginClass.getAnnotation(Component.class);
            if(annotation != null) {
                classList.add(pluginClass);
            }

            Service service = pluginClass.getAnnotation(Service.class);
            if(service != null) {
                classList.add(pluginClass);
            }
        }
        return classList;
    }
}
