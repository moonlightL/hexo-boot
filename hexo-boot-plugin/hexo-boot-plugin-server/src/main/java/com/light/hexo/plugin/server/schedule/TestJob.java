package com.light.hexo.plugin.server.schedule;

import com.light.hexo.common.plugin.annotation.HexoBootTask;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @Author MoonlightL
 * @ClassName: TestJob
 * @ProjectName hexo-boot
 * @Description: 测试定时器
 * @DateTime 2023/2/24, 0024 16:06
 */
@HexoBootTask
public class TestJob {

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void hello() {
        // todo something
    }
}
