package com.light.core.test;

import com.light.hexo.HexoBootApplication;
import com.light.hexo.common.event.VisitEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;

/**
 * @Author MoonlightL
 * @ClassName: EventTest
 * @ProjectName hexo-boot
 * @Description: 事件测试
 * @DateTime 2022/4/25, 0025 10:30
 */
@SpringBootTest(classes = HexoBootApplication.class)
public class EventTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void test() throws IOException {
        System.out.println("====== EventTest:=======" + Thread.currentThread().getName());
        this.applicationEventPublisher.publishEvent(new VisitEvent(this, "192.168.1.1", "chromw"));
        System.in.read();
    }
}
