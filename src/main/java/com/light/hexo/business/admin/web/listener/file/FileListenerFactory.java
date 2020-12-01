package com.light.hexo.business.admin.web.listener.file;

import com.light.hexo.business.admin.service.ThemeService;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: MoonlightL
 * @ClassName: FileListenerFactory
 * @ProjectName: hexo-boot
 * @Description: 文件监听器工厂
 * @DateTime: 2020/10/3 9:36 上午
 */
@Component
public class FileListenerFactory {

    // 设置轮询间隔
    private final long interval = TimeUnit.SECONDS.toSeconds(1);

    @Autowired
    private ThemeService themeService;

    public FileAlterationMonitor getMonitor() {
        // 创建过滤器
        IOFileFilter directories = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), HiddenFileFilter.VISIBLE);
        IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.nameFileFilter("theme.json"));
        IOFileFilter filter = FileFilterUtils.or(directories, files);

        // 装配过滤器
        File dir = this.themeService.getThemeCatalog();
        FileAlterationObserver observer = new FileAlterationObserver(dir, filter);

        // 向监听者添加监听器，并注入业务服务
        observer.addListener(new FileListener(themeService));

        // 返回监听者
        return new FileAlterationMonitor(interval, observer);
    }

}
