package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.plugin.annotation.HexoBootTask;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: ScheduleRegistry
 * @ProjectName hexo-boot
 * @Description: 定时器注册器
 * @DateTime 2022/4/24, 0024 16:49
 */
public class ScheduleRegistry extends AbstractModuleRegistry {

    private ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor;

    public ScheduleRegistry(HexoBootPluginManager pluginManager) {
        super(pluginManager);
        this.scheduledAnnotationBeanPostProcessor = super.beanFactory.getBean(ScheduledAnnotationBeanPostProcessor.class);
    }

    @SneakyThrows
    @Override
    public void register(String pluginId) {
        for (Class<?> clazz : this.listPluginTaskClasses(pluginId)) {
            this.scheduledAnnotationBeanPostProcessor.postProcessAfterInitialization(super.registryBean(clazz), clazz.getName());
        }
    }

    @SneakyThrows
    @Override
    public void unRegister(String pluginId) {
        for (Class<?> clazz : this.listPluginTaskClasses(pluginId)) {
            super.destroyBean(clazz);
        }
    }

    protected List<Class<?>> listPluginTaskClasses(String pluginId) throws Exception {
        return super.getPluginClasses(pluginId).stream().filter(item -> item.getAnnotation(HexoBootTask.class) != null).collect(Collectors.toList());
    }
}
