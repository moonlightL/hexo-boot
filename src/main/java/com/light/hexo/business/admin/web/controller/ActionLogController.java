package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.ActionLog;
import com.light.hexo.business.admin.model.Category;
import com.light.hexo.business.admin.service.ActionLogService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.model.ActionLogRequest;
import com.light.hexo.common.model.CategoryRequest;
import com.light.hexo.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: ActionLogController
 * @ProjectName hexo-boot
 * @Description: 操作日志控制器
 * @DateTime 2021/7/7 18:32
 */
@RequestMapping("/admin/actionLog")
@Controller
public class ActionLogController extends BaseController {

    @Autowired
    private ActionLogService actionLogService;

    /**
     * 日志详情
     * @param id
     * @param map
     * @return
     */
    @RequestMapping("detailUI.html")
    public String detailUI(Integer id, Map<String, Object> map) {
        ActionEnum[] values = ActionEnum.values();
        Map<Integer, String> actionTypeMap = Arrays.stream(values).collect(Collectors.toMap(ActionEnum::getCode, ActionEnum::getMessage));
        map.put("actionTypeMap", actionTypeMap);

        ActionLog actionLog = this.actionLogService.getActionLogInfo(id);
        map.put("actionLog", actionLog);
        return this.render("detailUI", map);
    }

    /**
     * 列表页
     * @param map
     * @return
     */
    @RequestMapping("listUI.html")
    public String listUI(Map<String, Object> map) {
        ActionEnum[] values = ActionEnum.values();
        Map<Integer, String> actionTypeMap = Arrays.stream(values).collect(Collectors.toMap(ActionEnum::getCode, ActionEnum::getMessage));
        map.put("actionTypeMap", actionTypeMap);
        return this.render("listUI", map);
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(ActionLogRequest request) {
        PageInfo<ActionLog> pageInfo = this.actionLogService.findPage(request);
        return Result.success(pageInfo);
    }

}
