package com.light.hexo.common.plugin.rewrite;

import org.pf4j.PluginRuntimeException;
import org.pf4j.PropertiesPluginDescriptorFinder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author MoonlightL
 * @ClassName: HexoBootPropertiesPluginDescriptorFinder
 * @ProjectName hexo-boot
 * @Description: 重写 PluginDescriptorFinder,处理卸载插件资源依然被占用问题
 * @DateTime 2022/4/23, 0023 23:34
 */
public class HexoBootPropertiesPluginDescriptorFinder extends PropertiesPluginDescriptorFinder {

    protected Path getPropertiesPath(Path pluginPath, String propertiesFileName) {
        if (Files.isDirectory(pluginPath)) {
            return pluginPath.resolve(Paths.get(propertiesFileName));
        } else {
            try {
                // rewrite these
                return HexoBootPluginFileUtil.getPath(pluginPath, propertiesFileName);
            } catch (IOException e) {
                throw new PluginRuntimeException(e);
            }
        }
    }
}
