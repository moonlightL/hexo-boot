package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.service.ThemeService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.model.ThemeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
