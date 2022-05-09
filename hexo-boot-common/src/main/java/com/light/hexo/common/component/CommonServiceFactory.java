package com.light.hexo.common.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * @Author: MoonlightL
 * @ClassName: CommonServiceFactory
 * @ProjectName: hexo-boot
 * @Description: 通用 Service  工厂
 * @DateTime: 2022-05-02 14:39
 */
@Slf4j
public abstract class CommonServiceFactory<T extends CommonService> implements ApplicationContextAware {

    /**
     * 子类实现
     * @return
     */
    protected abstract Map<String, T> getServiceMap();

    private Class<T> serviceClass;

    public CommonServiceFactory() {
        this.serviceClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, T> beansOfType = applicationContext.getBeansOfType(this.serviceClass);
        beansOfType.forEach((k, v) -> getServiceMap().put(v.getCode(), v));
    }

    /**
     * 获取实例
     * @param code
     * @return
     */
    public T getService(String code) {
        return getServiceMap().get(code);
    }

    /**
     * 添加实例
     * @param service
     */
    public void addService(T service) {
        log.info("======= class：{} 添加实例 ========", getClass());
        getServiceMap().put(service.getCode(), service);
    }

    /**
     * 删除实例
     * @param service
     */
    public void removeService(T service) {
        log.info("======= class：{} 删除实例 ========", getClass());
        getServiceMap().remove(service.getCode());
    }
}
