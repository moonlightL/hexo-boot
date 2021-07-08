package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.Category;
import com.light.hexo.business.admin.service.CategoryService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.CategoryRequest;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: CategoryController
 * @ProjectName hexo-boot
 * @Description: 分类控制器
 * @DateTime 2020/8/3 11:57
 */
@RequestMapping("/admin/category")
@Controller
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增页
     * @param resultMap
     * @return
     */
    @RequestMapping("addUI.html")
    public String addUI(Map<String, Object> resultMap) {
        return this.render("addUI", resultMap);
    }

    /**
     * 编辑页
     * @param resultMap
     * @return
     */
    @RequestMapping("editUI/{id}.html")
    public String editUI(@PathVariable Integer id, Map<String, Object> resultMap) {
        Category category = this.categoryService.findById(id);
        resultMap.put("vo", category);
        return this.render("editUI", resultMap);
    }

    /**
     * 新增分类
     * @param request
     * @return
     */
    @RequestMapping("add.json")
    @ResponseBody
    @OperateLog(value = "新增分类", actionType = ActionEnum.ADMIN_ADD)
    public Result add(@Validated(BaseRequest.Save.class) CategoryRequest request) {
        Category category = request.toDoModel();
        category.setName(category.getName().trim());
        category.setSort(Integer.valueOf(request.getSort()));
        this.categoryService.saveCategory(category);
        return Result.success();
    }

    /**
     * 编辑分类
     * @param request
     * @return
     */
    @RequestMapping("edit.json")
    @ResponseBody
    @OperateLog(value = "编辑分类", actionType = ActionEnum.ADMIN_EDIT)
    public Result edit(@Validated(BaseRequest.Update.class) CategoryRequest request) {
        Category category = request.toDoModel();
        category.setName(category.getName().trim());
        category.setSort(Integer.valueOf(request.getSort()));
        this.categoryService.updateCategory(category);
        return Result.success();
    }

    /**
     * 修改状态
     * @param request
     * @return
     */
    @RequestMapping("updateState.json")
    @ResponseBody
    @OperateLog(value = "修改分类状态", actionType = ActionEnum.ADMIN_EDIT)
    public Result updateState(CategoryRequest request) {
        this.categoryService.updateModel(request.toDoModel());
        return Result.success();
    }

    /**
     * 删除分类
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    @OperateLog(value = "删除分类", actionType = ActionEnum.ADMIN_REMOVE)
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        this.categoryService.removeCategoryBatch(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(CategoryRequest request) {
        PageInfo<Category> pageInfo = this.categoryService.findPage(request);
        return Result.success(pageInfo);
    }
}
