package com.light.hexo.common.plugin.config;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.config.BlogConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.File;
import java.nio.file.Paths;

/**
 * @Author MoonlightL
 * @ClassName: PluginConfig
 * @ProjectName hexo-boot
 * @Description: 插件配置
 * @DateTime 2022/4/19, 0019 20:01
 */
@Configuration
public class PluginConfig {

    @Bean
    public HexoBootPluginManager pluginManager(BlogConfig blogConfig) {
        String pluginDirPath = blogConfig.getPluginDir();
        File pluginDir = new File(pluginDirPath);
        if (!pluginDir.exists()) {
            pluginDir.mkdirs();
        }
        return new HexoBootPluginManager(Paths.get(blogConfig.getPluginDir()));
    }
}
