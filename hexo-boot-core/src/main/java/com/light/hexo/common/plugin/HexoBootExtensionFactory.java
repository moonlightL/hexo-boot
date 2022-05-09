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
     * 销毁扩展实例
     * @param extensionClass
     */
    void destroy(Class<?> extensionClass);
}
