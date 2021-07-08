package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.Dynamic;
import com.light.hexo.business.admin.service.DynamicService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.DynamicRequest;
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
 * @ClassName: DynamicController
 * @ProjectName hexo-boot
 * @Description: 动态控制器
 * @DateTime 2021/6/23 15:40
 */
@RequestMapping("/admin/dynamic")
@Controller
public class DynamicController extends BaseController {

    @Autowired
    private DynamicService dynamicService;

    /**
     * 发表页面
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
        Dynamic dynamic = this.dynamicService.findById(id);
        resultMap.put("vo", dynamic);
        return this.render("editUI", resultMap);
    }

    /**
     * 发表
     * @param request
     * @return
     */
    @RequestMapping("add.json")
    @ResponseBody
    @OperateLog(value = "发表动态", actionType = ActionEnum.ADMIN_ADD)
    public Result add(@Validated(BaseRequest.Save.class) DynamicRequest request) {
        Dynamic dynamic = request.toDoModel();
        this.dynamicService.saveDynamic(dynamic);
        return Result.success();
    }

    /**
     * 编辑动态
     * @param request
     * @return
     */
    @RequestMapping("edit.json")
    @ResponseBody
    @OperateLog(value = "编辑动态", actionType = ActionEnum.ADMIN_EDIT)
    public Result edit(@Validated(BaseRequest.Update.class) DynamicRequest request) {
        Dynamic dynamic = request.toDoModel();
        this.dynamicService.updateDynamic(dynamic);
        return Result.success();
    }

    /**
     * 删除动态
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    @OperateLog(value = "删除动态", actionType = ActionEnum.ADMIN_REMOVE)
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        this.dynamicService.removeDynamicBatch(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(DynamicRequest request) {
        PageInfo<Dynamic> pageInfo = this.dynamicService.findPage(request);
        return Result.success(pageInfo);
    }
}
