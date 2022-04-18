package com.light.hexo.common.extension;

import org.pf4j.ExtensionPoint;
import org.springframework.context.ApplicationContext;

/**
 * @Author MoonlightL
 * @ClassName: HexoBootExtensionPoint
 * @ProjectName hexo-boot
 * @Description: 插件扩展
 * @DateTime 2022/4/12, 0012 17:10
 */
public interface HexoBootExtensionPoint extends ExtensionPoint {

    void action(ApplicationContext applicationContext);

    void retreat(ApplicationContext applicationContext);
}
