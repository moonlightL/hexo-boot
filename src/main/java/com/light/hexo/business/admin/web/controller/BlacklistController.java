package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.model.Blacklist;
import com.light.hexo.business.admin.service.BlacklistService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.BlackListRequest;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: BlacklistController
 * @ProjectName hexo-boot
 * @Description: 黑名单控制器
 * @DateTime 2020/9/9 15:44
 */
@Controller
@RequestMapping("/admin/blacklist")
public class BlacklistController extends BaseController {

    @Autowired
    private BlacklistService blacklistService;

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
     * @param id
     * @param map
     * @return
     * @throws GlobalException
     */
    @GetMapping("/editUI/{id}.html")
    public String updateUI(@PathVariable("id") Long id, Map<String,Object> map) throws GlobalException {
        Blacklist blacklist = this.blacklistService.findById(id);
        if (blacklist == null) {
            ExceptionUtil.throwExToPage(HexoExceptionEnum.ERROR_BLACKLIST_NOT_EXIST);
        }

        map.put("vo", blacklist);
        return render("editUI", map);
    }

    /**
     * 新增黑名单
     * @param request
     * @return
     */
    @RequestMapping("add.json")
    @ResponseBody
    public Result add(@Validated(BaseRequest.Save.class) BlackListRequest request) {
        this.blacklistService.saveModel(request.toDoModel());
        return Result.success();
    }

    /**
     * 编辑黑名单
     * @param request
     * @return
     */
    @RequestMapping("edit.json")
    @ResponseBody
    public Result edit(@Validated(BaseRequest.Update.class) BlackListRequest request) {
        this.blacklistService.updateModel(request.toDoModel());
        return Result.success();
    }

    /**
     * 删除黑名单
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        this.blacklistService.removeBatch(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(BlackListRequest request) {
        PageInfo<Blacklist> pageInfo = this.blacklistService.findPage(request);
        return Result.success(pageInfo);
    }

}
