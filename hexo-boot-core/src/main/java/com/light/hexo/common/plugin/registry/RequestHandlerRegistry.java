package com.light.hexo.common.plugin.registry;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: RequestHandlerRegistry
 * @ProjectName hexo-boot
 * @Description: 请求处理器注册器
 * @DateTime 2022/4/19, 0019 20:37
 */
public class RequestHandlerRegistry extends AbstractModuleRegistry {

    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private Method getMappingForMethod;

    public RequestHandlerRegistry(HexoBootPluginManager pluginManager) {
        super(pluginManager);
        this.requestMappingHandlerMapping = super.pluginManager.getApplicationContext().getBean(RequestMappingHandlerMapping.class);
        this.getMappingForMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class, "getMappingForMethod", Method.class, Class.class);
        this.getMappingForMethod.setAccessible(true);
    }

    @SneakyThrows
    @Override
    public void register(String pluginId) {
        for (Class<?> clazz : super.getPluginClasses(pluginId)) {
            Controller annotation = clazz.getAnnotation(Controller.class);
            RestController restAnnotation = clazz.getAnnotation(RestController.class);
            if(annotation != null || restAnnotation != null) {
                Object bean = super.registryBean(clazz);
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(RequestMapping.class) != null ||
                        method.getAnnotation(GetMapping.class) != null ||
                        method.getAnnotation(PostMapping.class) != null) {
                        RequestMappingInfo requestMappingInfo = (RequestMappingInfo) this.getMappingForMethod.invoke(this.requestMappingHandlerMapping, method, clazz);
                        this.requestMappingHandlerMapping.registerMapping(requestMappingInfo, bean, method);
                    }
                }
            }
        }

    }

    @SneakyThrows
    @Override
    public void unRegister(String pluginId) {
        for (RequestMappingInfo requestMappingInfo : this.getRequestMappingInfo(pluginId)) {
            this.requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
        }
    }

    private List<RequestMappingInfo> getRequestMappingInfo(String pluginId) throws Exception {
        List<RequestMappingInfo> requestMappingInfoList = new ArrayList<>();
        List<Class<?>> pluginClassList = super.getPluginClasses(pluginId);
        for (Class<?> clazz : pluginClassList) {
            Controller annotation = clazz.getAnnotation(Controller.class);
            RestController restAnnotation = clazz.getAnnotation(RestController.class);
            if (annotation != null || restAnnotation != null) {
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    RequestMappingInfo requestMappingInfo = (RequestMappingInfo) this.getMappingForMethod.invoke(this.requestMappingHandlerMapping, method, clazz);
                    requestMappingInfoList.add(requestMappingInfo);
                }
                super.destroyBean(clazz);
            }
        }
        return requestMappingInfoList;
    }
}
