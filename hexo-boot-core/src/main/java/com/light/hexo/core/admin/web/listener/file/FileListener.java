package com.light.hexo.web.listener.file;

import com.light.hexo.mapper.model.Theme;
import com.light.hexo.mapper.model.extend.ThemeFile;
import com.light.hexo.core.admin.service.ThemeService;
import com.light.hexo.common.util.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import java.io.File;
import java.util.*;

/**
 * @Author: MoonlightL
 * @ClassName: FileListener
 * @ProjectName: hexo-boot
 * @Description: 文件监听器
 * @DateTime: 2020/10/3 9:27 上午
 */
@Slf4j
public class FileListener extends FileAlterationListenerAdaptor {

    private final ThemeService themeService;

    public FileListener(ThemeService themeService) {
        this.themeService = themeService;
    }

    /**
     * 文件创建
     * @param file
     */
    @SneakyThrows
    @Override
    public void onFileCreate(File file) {
        log.info("===================== onFileCreate start ==============================");
        // 读取内容
        String content = FileUtils.readFileToString(file, "UTF-8");
        ThemeFile themeFile = JsonUtil.string2Obj(content, ThemeFile.class);

        String fileDir = file.getParentFile().getName();
        Integer themeId = this.themeService.saveTheme(
                themeFile.getName(),
                String.format("/theme/%s/preview.png", fileDir),
                false,
                StringUtils.isNotBlank(themeFile.getRemark()) ? themeFile.getRemark(): "",
                themeFile.getExtension()
        );

        Theme activeTheme = this.themeService.getActiveTheme(false);
        if (activeTheme == null) {
            this.themeService.useTheme(themeId);
        }
        log.info("===================== onFileCreate end ==============================");
    }

    /**
     * 文件修改
     * @param file
     */
    @SneakyThrows
    @Override
    public void onFileChange(File file) {
        log.info("===================== onFileChange start filename:{} ==============================", file.getName());

        if (!file.exists()) {
            log.info("===================== onFileChange file {} not exist ==============================", file.getName());
            return;
        }

        Theme activeTheme = this.themeService.getActiveTheme(false);

       // 读取内容
        String content = FileUtils.readFileToString(file, "UTF-8");
        ThemeFile themeFile = JsonUtil.string2Obj(content, ThemeFile.class);

        boolean state = activeTheme.getName().equals(themeFile.getName());

        String fileDir = file.getParentFile().getName();
        Integer themeId = this.themeService.saveTheme(
                themeFile.getName(),
                String.format("/theme/%s/preview.png", fileDir),
                state,
                StringUtils.isNotBlank(themeFile.getRemark()) ? themeFile.getRemark(): "",
                themeFile.getExtension()
        );

        activeTheme = this.themeService.getActiveTheme(false);
        if (activeTheme == null) {
            this.themeService.useTheme(themeId);
        }
        log.info("===================== onFileChange end ==============================");
    }

    /**
     * 文件删除
     * @param file
     */
    @Override
    public void onFileDelete(File file) {
        log.info("===================== onFileDelete start filename: {}==============================", file.getName());

        String fileDir = file.getParentFile().getName();
        Theme dbTheme = this.themeService.getTheme(fileDir);
        if (dbTheme == null) {
            log.info("===================== onFileDelete theme {} not exist ==============================", fileDir);
            return;
        }

        this.themeService.deleteThemeBatch(Collections.singletonList(dbTheme.getId()));

        Theme activeTheme = this.themeService.getActiveTheme(false);
        if (activeTheme == null || activeTheme.getId().equals(dbTheme.getId())) {
            List<Theme> allList = this.themeService.findAll();
            if (!CollectionUtils.isEmpty(allList)) {
                Theme theme = allList.get(0);
                this.themeService.useTheme(theme.getId());
            }
        }

        log.info("===================== onFileDelete end ==============================");
    }
}
