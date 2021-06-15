package com.light.hexo.business.admin.web.listener.file;

import com.light.hexo.business.admin.service.ThemeService;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;

/**
 * @Author: MoonlightL
 * @ClassName: FileListenerFactory
 * @ProjectName: hexo-boot
 * @Description: 文件监听器工厂
 * @DateTime: 2020/10/3 9:36 上午
 */
@Component
public class FileListenerFactory {

    @Autowired
    private ThemeService themeService;

    public FileAlterationMonitor createMonitor() {

        // 创建过滤器
        IOFileFilter directories = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), HiddenFileFilter.VISIBLE);
        IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.nameFileFilter("theme.json"));
        IOFileFilter filter = FileFilterUtils.or(directories, files);

        // 观察者
        File dir = this.themeService.getThemeCatalog(false);
        FileAlterationObserver observer = new FileAlterationObserver(dir, filter);

        // 监听器
        FileListener fileListener = new FileListener(themeService);

        // 注册监听器
        observer.addListener(fileListener);

        // 返回监听者
        return new FileAlterationMonitor(1000, observer);
    }

}
