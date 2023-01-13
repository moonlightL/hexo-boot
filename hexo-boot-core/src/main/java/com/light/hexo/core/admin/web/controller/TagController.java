package com.light.hexo.core.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.common.vo.Result;
import com.light.hexo.mapper.model.Tag;
import com.light.hexo.core.admin.service.TagService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.request.TagRequest;
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
 * @ClassName: TagController
 * @ProjectName hexo-boot
 * @Description: 标签控制器
 * @DateTime 2020/8/3 16:31
 */
@RequestMapping("/admin/tag")
@Controller
public class TagController extends BaseController {

    @Autowired
    private TagService tagService;

    /**
     * 编辑页
     * @param resultMap
     * @return
     */
    @RequestMapping("editUI/{id}.html")
    public String editUI(@PathVariable Integer id, Map<String, Object> resultMap) {
        Tag tag = this.tagService.findById(id);
        resultMap.put("vo", tag);
        return this.render("editUI", resultMap);
    }

    /**
     * 编辑标签
     * @param request
     * @return
     */
    @RequestMapping("edit.json")
    @ResponseBody
    @OperateLog(value = "编辑标签", actionType = ActionEnum.ADMIN_EDIT)
    public Result edit(@Validated(BaseRequest.Update.class) TagRequest request) {
        this.tagService.updateModel(request.toDoModel());
        return Result.success();
    }

    /**
     * 删除标签
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    @OperateLog(value = "删除标签", actionType = ActionEnum.ADMIN_REMOVE)
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        this.tagService.removeTagBatch(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(TagRequest request) {
        PageInfo<Tag> pageInfo = this.tagService.findPage(request);
        return Result.success(pageInfo);
    }

    /**
     * 获取全部列表
     * @param request
     * @return
     */
    @RequestMapping("getAll.json")
    @ResponseBody
    public Result getAll(TagRequest request) {
        return Result.success(this.tagService.findAll(request));
    }
}
