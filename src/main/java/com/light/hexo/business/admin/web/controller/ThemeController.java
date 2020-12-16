package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.model.Attachment;
import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.model.ThemeExtend;
import com.light.hexo.business.admin.service.ThemeExtendService;
import com.light.hexo.business.admin.service.ThemeService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.model.ThemeRequest;
import com.light.hexo.common.model.TreeNode;
import com.light.hexo.common.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: ThemeController
 * @ProjectName hexo-boot
 * @Description: 主题控制器
 * @DateTime 2020/9/24 14:48
 */
@RequestMapping("/admin/theme")
@Controller
public class ThemeController extends BaseController {

    @Autowired
    private ThemeService themeService;

    @Autowired
    private ThemeExtendService themeExtendService;

    /**
     * 拉取主题
     * @param resultMap
     * @return
     */
    @GetMapping("/fetchUI.html")
    public String fetchUI(Map<String,Object> resultMap) {
        return render("fetchUI", resultMap);
    }

    /**
     * 主题配置页
     * @param id
     * @param resultMap
     * @return
     * @throws GlobalException
     */
    @GetMapping("/configUI.html")
    public String configUI(Integer id, Map<String,Object> resultMap) {
        Theme theme = this.themeService.findById(id);
        if (theme == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_NOT_EXIST);
        }

        List<ThemeExtend> extendList = this.themeExtendService.listThemeExtends(theme.getId());

        resultMap.put("vo", theme);
        resultMap.put("extendList", extendList);
        return render("configUI", resultMap);
    }

    /**
     * 源码页
     * @param id
     * @param resultMap
     * @return
     * @throws GlobalException
     */
    @GetMapping("/codeUI.html")
    public String codeUI(Integer id, Map<String,Object> resultMap) {
        Theme theme = this.themeService.findById(id);
        if (theme == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_NOT_EXIST);
        }

        resultMap.put("theme", theme);

        List<TreeNode> catalogList = this.themeService.getThemeTreeNode(theme);
        resultMap.put("catalogList", catalogList);

        return render("codeUI", resultMap);
    }

    /**
     * 使用主题
     * @param request
     * @return
     */
    @RequestMapping("/useTheme.json")
    @ResponseBody
    public Result useTheme(@Validated(BaseRequest.Update.class) ThemeRequest request) {
        Theme theme = request.toDoModel();
        this.themeService.useTheme(theme.getId());
        return Result.success();
    }

    /**
     * 拉取主题
     * @param themeUrl
     * @return
     */
    @RequestMapping("/fetchTheme.json")
    @ResponseBody
    public Result fetchTheme(@RequestParam String themeUrl) {
        this.themeService.fetchTheme(themeUrl);
        return Result.success();
    }

    /**
     * 检测主题
     * @param themeName
     * @return
     */
    @RequestMapping("/checkThemeName.json")
    @ResponseBody
    public Result checkThemeName(@RequestParam String themeName) {
        Theme theme = this.themeService.getTheme(themeName);
        return Result.success(theme != null);
    }

    /**
     * 保存主题配置
     * @param extendList
     * @return
     */
    @RequestMapping("saveConfig.json")
    @ResponseBody
    public Result saveConfig(@RequestBody List<ThemeExtend> extendList) {
        this.themeExtendService.saveThemeExtend(extendList);
        return Result.success();
    }

    /**
     * 获取主题源码
     * @param path
     * @return
     */
    @RequestMapping("getCode.json")
    @ResponseBody
    public Result getCode(String path) throws IOException {
        String content = this.themeService.getThemeFileContent(path);
        return Result.success(content);
    }

    /**
     * 编辑主题源码
     * @param path
     * @param content
     * @return
     */
    @RequestMapping("editCode.json")
    @ResponseBody
    public Result editCode(String path, String content) throws IOException {
        this.themeService.editThemeFileContent(path, content);
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(ThemeRequest request) {
        PageInfo<Theme> pageInfo = this.themeService.findPage(request);
        return Result.success(pageInfo);
    }
}
