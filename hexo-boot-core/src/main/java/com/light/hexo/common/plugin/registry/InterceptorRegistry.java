package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.plugin.annotation.HexoBootIntercept;
import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * @Author MoonlightL
 * @ClassName: InterceptorRegistry
 * @ProjectName hexo-boot
 * @Description: 拦截器注册器
 * @DateTime 2022/4/20, 0020 10:32
 */
public class InterceptorRegistry extends AbstractModuleRegistry {

    private AbstractHandlerMapping handlerMapping;

    private List<HandlerInterceptor> handlerInterceptorList;

    public InterceptorRegistry(HexoBootPluginManager pluginManager) {
        super(pluginManager);
        this.handlerMapping = super.pluginManager.getApplicationContext().getBean(AbstractHandlerMapping.class);
        Field adaptedInterceptorsField = ReflectionUtils.findField(this.handlerMapping.getClass(), "adaptedInterceptors", List.class);
        adaptedInterceptorsField.setAccessible(true);
        try {
            this.handlerInterceptorList = (List<HandlerInterceptor>) adaptedInterceptorsField.get(this.handlerMapping);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void register(String pluginId) {
        for (Class<?> clazz : super.getPluginClasses(pluginId)) {
            HexoBootIntercept intercept = clazz.getAnnotation(HexoBootIntercept.class);
            if(intercept != null && intercept.value() != null) {
                HandlerInterceptor handlerInterceptor = (HandlerInterceptor) super.registryBean(clazz);
                MappedInterceptor mappedInterceptor = new MappedInterceptor(intercept.value(), handlerInterceptor);
                this.handlerInterceptorList.add(mappedInterceptor);
            }
        }
    }

    @SneakyThrows
    @Override
    public void unRegister(String pluginId) {
        for (Class<?> clazz : super.getPluginClasses(pluginId)) {
            HexoBootIntercept intercept = clazz.getAnnotation(HexoBootIntercept.class);
            if(intercept != null && intercept.value() != null) {
                for (HandlerInterceptor handlerInterceptor : this.handlerInterceptorList) {
                    if(handlerInterceptor instanceof MappedInterceptor) {
                        MappedInterceptor mappedInterceptor = (MappedInterceptor) handlerInterceptor;
                        if(Objects.equals(super.pluginManager.getExtensionFactory().create(clazz), mappedInterceptor.getInterceptor())) {
                            this.handlerInterceptorList.remove(handlerInterceptor);
                            super.destroyBean(clazz);
                            break;
                        }
                    }
                }
            }
        }
    }
}
