package com.light.hexo.business.admin.web.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.service.ConfigService;
import com.light.hexo.business.admin.service.ThemeService;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.JsonUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: MoonlightL
 * @ClassName: CheckThemeListener
 * @ProjectName: hexo-boot
 * @Description: 启动检测主题定时器
 * @DateTime: 2020/10/5 10:53 下午
 */
@Component
public class CheckThemeListener {

    @Autowired
    private ThemeService themeService;

    public void checkTheme() throws Exception {

        File dir = ResourceUtils.getFile("classpath:templates/theme");
        if (dir.list() == null || dir.listFiles().length == 0) {
            return;
        }

        Theme activeTheme = this.themeService.getActiveTheme(false);

        // 实际存在的主题名称
        List<String> themeNameList = new ArrayList<>();
        for (File file : dir.listFiles()) {

            File[] jsonFiles = file.listFiles((i, name) -> name.equals("theme.json"));
            if (jsonFiles == null) {
                continue;
            }

            File jsonFile = jsonFiles[0];

            // 读取内容
            String content = FileUtils.readFileToString(jsonFile, "UTF-8");
            Map<String, Object> map = JsonUtil.string2Obj(content, new TypeReference<Map<String, Object>>() {});

            if (Objects.isNull(map.get("name"))) {
                continue;
            }

            String themeName = map.get("name").toString();
            themeNameList.add(themeName);

            boolean state = false;
            if (activeTheme == null) {
                if (themeName.equals("default")) {
                    state = true;
                }
            } else {
                if (activeTheme.getName().equals(themeName)) {
                    state = true;
                }
            }

            String fileDir = jsonFile.getParentFile().getName();
            this.themeService.saveTheme(
                    themeName,
                    String.format("/theme/%s/preview.png", fileDir),
                    state,
                    Objects.nonNull(map.get("remark")) ? map.get("remark").toString(): "",
                    (List<Map<String, String>>)map.get("extension")
            );

        }

        // 过滤出不在 theme 目录的主题记录
        List<Theme> themeList = this.themeService.findAll();
        List<Theme> filterList = themeList.stream().filter(i -> !themeNameList.contains(i.getName())).collect(Collectors.toList());
        this.themeService.deleteThemeBatch(filterList);
    }
}
