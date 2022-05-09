package com.light.hexo.core.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.common.vo.Result;
import com.light.hexo.common.constant.HexoExceptionEnum;
import com.light.hexo.mapper.model.Theme;
import com.light.hexo.mapper.model.ThemeExtend;
import com.light.hexo.core.admin.service.ThemeExtendService;
import com.light.hexo.core.admin.service.ThemeService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.request.ThemeRequest;
import com.light.hexo.common.request.TreeNode;
import com.light.hexo.common.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
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
     * 解压主题
     * @param resultMap
     * @return
     */
    @GetMapping("/unzipUI.html")
    public String unzipUI(Map<String,Object> resultMap) {
        return render("unzipUI", resultMap);
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

        return render("codeUI", resultMap);
    }

    @RequestMapping("/getCatalogList.json")
    @ResponseBody
    public Result getCatalogList(Integer id) {
        Theme theme = this.themeService.findById(id);
        if (theme == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_NOT_EXIST);
        }

        List<TreeNode> catalogList = this.themeService.getThemeTreeNode(theme);
        return Result.success(catalogList);
    }


    /**
     * 切换主题
     * @param request
     * @return
     */
    @RequestMapping("/useTheme.json")
    @ResponseBody
    @OperateLog(value = "切换主题", actionType = ActionEnum.ADMIN_EDIT)
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
    @OperateLog(value = "拉取主题", actionType = ActionEnum.ADMIN_ADD)
    public Result fetchTheme(@RequestParam String themeUrl) {
        boolean result = this.themeService.fetchTheme(themeUrl);
        return result ? Result.success() : Result.fail();
    }

    /**
     * 解压主题
     * @param file
     * @return
     */
    @RequestMapping("/unzipTheme.json")
    @ResponseBody
    @OperateLog(value = "解压主题", actionType = ActionEnum.ADMIN_ADD)
    public Result unzipTheme(MultipartFile file) {

        if (file == null) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        String contentType = file.getContentType();
        if (!"application/x-zip-compressed".equalsIgnoreCase(contentType) && !"application/zip".equalsIgnoreCase(contentType)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        String themeName = "";
        try {
            InputStream inputStream = file.getInputStream();
            themeName = this.themeService.unzipTheme(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.success(themeName);
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
     * 编辑主题配置
     * @param extendList
     * @return
     */
    @RequestMapping("saveConfig.json")
    @ResponseBody
    @OperateLog(value = "编辑主题配置", actionType = ActionEnum.ADMIN_EDIT)
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
    @OperateLog(value = "编辑主题源码", actionType = ActionEnum.ADMIN_EDIT)
    public Result editCode(String path, String content) throws IOException {
        this.themeService.editThemeFileContent(path, content);
        return Result.success();
    }

    /**
     * 删除主题
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping("remove.json")
    @ResponseBody
    @OperateLog(value = "删除主题", actionType = ActionEnum.ADMIN_REMOVE)
    public Result remove(Integer id) {
        this.themeService.removeTheme(id);
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
