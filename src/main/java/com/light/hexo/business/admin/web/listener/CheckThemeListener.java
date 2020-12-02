package com.light.hexo.business.admin.web.listener;

import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.core.type.TypeReference;
import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.service.ThemeService;
import com.light.hexo.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
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
@Slf4j
public class CheckThemeListener {

    @Autowired
    private ThemeService themeService;

    public void checkTheme() throws Exception {
        // 获取主题目录
        File dir = this.themeService.getThemeCatalog();
        if (dir == null) {
            return;
        }

        // 实际存在的主题名称
        List<String> themeNameList = new ArrayList<>();
        for (File file : dir.listFiles()) {

            File[] jsonFiles = file.listFiles((i, name) -> name.equals("theme.json"));
            if (jsonFiles == null) {
                continue;
            }

            File jsonFile = jsonFiles[0];

            Theme activeTheme = this.themeService.getActiveTheme(false);

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
            Integer themeId = this.themeService.saveTheme(
                    themeName,
                    String.format("/theme/%s/preview.png", fileDir),
                    state,
                    Objects.nonNull(map.get("remark")) ? map.get("remark").toString(): "",
                    (List<Map<String, String>>) map.get("extension")
            );

            if (state) {
                this.themeService.useTheme(themeId);
            }
        }

        // 过滤出不在 theme 目录的主题记录
        List<Theme> themeList = this.themeService.findAll();
        List<Theme> filterList = themeList.stream().filter(i -> !themeNameList.contains(i.getName())).collect(Collectors.toList());
        this.themeService.deleteThemeBatch(filterList);
    }
}
