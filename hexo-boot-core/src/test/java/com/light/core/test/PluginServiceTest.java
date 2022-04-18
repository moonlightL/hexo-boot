package com.light.core.test;

import com.light.hexo.HexoBootApplication;
import com.light.hexo.common.extension.HexoBootExtensionPoint;
import com.light.hexo.common.util.SpringContextUtil;
import org.junit.jupiter.api.Test;
import org.pf4j.JarPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.boot.test.context.SpringBootTest;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author MoonlightL
 * @ClassName: PluginService
 * @ProjectName hexo-boot
 * @Description: 插件测试
 * @DateTime 2022/4/12, 0012 18:15
 */
@SpringBootTest(classes = HexoBootApplication.class)
public class PluginServiceTest {

    @Test
    public void test() throws InterruptedException {

        PluginManager pluginManager = SpringContextUtil.getBean(PluginManager.class);

        String pluginId = pluginManager.loadPlugin(Paths.get("D:\\data\\hexo-boot-plugin-server-1.0.0.jar"));

        pluginManager.startPlugin(pluginId);

        List<HexoBootExtensionPoint> extensions = pluginManager.getExtensions(HexoBootExtensionPoint.class);
        System.out.println(extensions);

    }
}
