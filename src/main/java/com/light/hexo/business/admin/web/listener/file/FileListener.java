package com.light.hexo.business.admin.web.listener.file;

import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.service.ThemeService;
import com.light.hexo.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     * 文件创建执行
     * @param file
     */
    @Override
    public void onFileCreate(File file) {

        try {
            // 读取内容
            String content = FileUtils.readFileToString(file, "UTF-8");
            Map<String, Object> map = JsonUtil.string2Obj(content, Map.class);

            this.themeService.saveTheme(
                    Objects.nonNull(map.get("name")) ? map.get("name").toString() : file.getParentFile().getName(),
                    Objects.nonNull(map.get("preview")) ? map.get("preview").toString(): "",
                    false,
                    Objects.nonNull(map.get("remark")) ? map.get("remark").toString(): ""
            );


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 文件创建修改
     * @param file
     */
    @Override
    public void onFileChange(File file) {
        log.info("========文件修改===========");
    }

    /**
     * 文件创建删除
     * @param file
     */
    @Override
    public void onFileDelete(File file) {

        String themeName = file.getParentFile().getName();
        Theme dbTheme = this.themeService.checkTheme(themeName);

        this.themeService.removeModel(dbTheme.getId());

        if (dbTheme.getState()) {
            List<Theme> themeList = this.themeService.findAll();
            if (!CollectionUtils.isEmpty(themeList)) {
                Theme theme = themeList.get(0);
                this.themeService.useTheme(theme);
            }
        }
    }

}
