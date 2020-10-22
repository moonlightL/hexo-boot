package com.light.hexo.business.admin.web.listener;

import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.service.ThemeService;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.JsonUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

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
            Map<String, Object> map = JsonUtil.string2Obj(content, Map.class);

            String themeName = Objects.nonNull(map.get("name")) ? map.get("name").toString() : file.getParentFile().getName();

            if (currentTheme != null && currentTheme.getName().equals(themeName)) {
                continue;
            }

            this.themeService.saveTheme(
                    themeName,
                    Objects.nonNull(map.get("preview")) ? map.get("preview").toString(): "",
                    false,
                    Objects.nonNull(map.get("remark")) ? map.get("remark").toString(): "");

        }

    }
}
