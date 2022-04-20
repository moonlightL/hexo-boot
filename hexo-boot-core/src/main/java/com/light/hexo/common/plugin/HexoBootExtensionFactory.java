package com.light.hexo.common.plugin;

/**
 * @Author MoonlightL
 * @ClassName: HexoBootExtensionFactory
 * @ProjectName hexo-boot
 * @Description: 扩展组件工厂
 * @DateTime 2022/4/19, 0019 20:30
 */
public interface HexoBootExtensionFactory {

    /**
     * 指定beanName创建Spring实例
     * @param beanName
     * @param extensionClass
     * @param <T>
     * @return
     */
    <T> T create(String beanName, Class<T> extensionClass);

    /**
     * 销毁扩展实例
     * @param extensionClass
     */
    void destroy(Class<?> extensionClass);
}
