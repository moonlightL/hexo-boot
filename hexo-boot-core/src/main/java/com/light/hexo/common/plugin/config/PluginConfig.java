package com.light.hexo.common.plugin.config;

import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.core.admin.config.BlogConfig;
import org.pf4j.PluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;
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
    public PluginManager pluginManager(BlogConfig blogConfig) {
        String pluginDirPath = blogConfig.getPluginDir();
        File pluginDir = new File(pluginDirPath);
        if (!pluginDir.exists()) {
            pluginDir.mkdirs();
        }
        Path pluginPath = Paths.get(blogConfig.getPluginDir());
        return new HexoBootPluginManager(pluginPath);
    }
}
