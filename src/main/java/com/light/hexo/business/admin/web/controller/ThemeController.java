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
import com.light.hexo.common.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
     * 使用主题
     * @param request
     * @return
     */
    @RequestMapping("/useTheme.json")
    @ResponseBody
    public Result useTheme(@Validated(BaseRequest.Update.class) ThemeRequest request) {
        Theme theme = request.toDoModel();
        this.themeService.useTheme(theme);
        return Result.success();
    }

    @GetMapping("/configUI.html")
    public String configUI(Integer id, Map<String,Object> resultMap) throws GlobalException {
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
