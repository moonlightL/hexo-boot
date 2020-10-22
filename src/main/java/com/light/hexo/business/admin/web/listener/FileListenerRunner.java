package com.light.hexo.business.admin.web.listener;

import com.light.hexo.business.admin.web.listener.file.FileListenerFactory;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author: MoonlightL
 * @ClassName: FileListenerRunner
 * @ProjectName: hexo-boot
 * @Description: 启动文件监听器
 * @DateTime: 2020/10/3 2:42 上午
 */
@Component
public class FileListenerRunner implements CommandLineRunner {

    @Autowired
    private CheckThemeListener checkThemeListener;

    @Autowired
    private FileListenerFactory fileListenerFactory;

    @Override
    public void run(String... args) throws Exception {

        try {
            // 启动线程，监听文件变化
            FileAlterationMonitor fileAlterationMonitor = fileListenerFactory.getMonitor();
            fileAlterationMonitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // 检测主题目录
            this.checkThemeListener.checkTheme();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
