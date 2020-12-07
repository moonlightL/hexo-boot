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
import java.util.Arrays;
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
     * 文件创建
     * @param file
     */
    @Override
    public void onFileCreate(File file) {

        try {
            // 读取内容
            String content = FileUtils.readFileToString(file, "UTF-8");
            Map<String, Object> map = JsonUtil.string2Obj(content, Map.class);

            String fileDir = file.getParentFile().getName();
            Integer themeId = this.themeService.saveTheme(
                    map.get("name").toString(),
                    String.format("/theme/%s/preview.png", fileDir),
                    false,
                    Objects.nonNull(map.get("remark")) ? map.get("remark").toString(): "",
                    (List<Map<String, String>>)map.get("extension")
            );

            Theme activeTheme = this.themeService.getActiveTheme(false);
            if (activeTheme == null) {
                this.themeService.useTheme(themeId);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 文件修改
     * @param file
     */
    @Override
    public void onFileChange(File file) {
        try {
            // 读取内容
            String content = FileUtils.readFileToString(file, "UTF-8");
            Map<String, Object> map = JsonUtil.string2Obj(content, Map.class);

            String fileDir = file.getParentFile().getName();
            this.themeService.saveTheme(
                    map.get("name").toString(),
                    String.format("/theme/%s/preview.png", fileDir),
                    false,
                    Objects.nonNull(map.get("remark")) ? map.get("remark").toString(): "",
                    (List<Map<String, String>>)map.get("extension")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件删除
     * @param file
     */
    @Override
    public void onFileDelete(File file) {
        String fileDir = file.getParentFile().getName();
        Theme dbTheme = this.themeService.checkTheme(fileDir);
        this.themeService.deleteThemeBatch(Arrays.asList(dbTheme));
    }

}
