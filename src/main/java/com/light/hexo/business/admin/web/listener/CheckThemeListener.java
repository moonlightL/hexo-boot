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
import java.util.Map;
import java.util.Objects;

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

        Theme currentTheme = this.themeService.getCurrentTheme();

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

            boolean state = false;
            if (currentTheme == null) {
                if (themeName.equals("default")) {
                    state = true;
                }
            } else {
                if (currentTheme.getName().equals(themeName)) {
                    state = true;
                }
            }

            String fileDir = jsonFile.getParentFile().getName();
            this.themeService.saveTheme(
                    themeName,
                    fileDir,
                    String.format("/theme/%s/preview.png", fileDir),
                    state,
                    Objects.nonNull(map.get("remark")) ? map.get("remark").toString(): "",
                    (Map<String, Object>)map.get("extension")
            );

        }

    }
}
