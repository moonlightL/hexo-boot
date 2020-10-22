package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.FriendLink;
import com.light.hexo.business.admin.service.FriendLinkService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.FriendLinkRequest;
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
 * @ClassName: FriendLinkController
 * @ProjectName hexo-boot
 * @Description: 友链控制器
 * @DateTime 2020/9/22 17:52
 */
@RequestMapping("/admin/friendLink")
@Controller
public class FriendLinkController extends BaseController {

    @Autowired
    private FriendLinkService friendLinkService;

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
        FriendLink friendLink = this.friendLinkService.findById(id);
        resultMap.put("vo", friendLink);
        return this.render("editUI", resultMap);
    }

    /**
     * 新增友链
     * @param request
     * @return
     */
    @RequestMapping("add.json")
    @ResponseBody
    public Result add(@Validated(BaseRequest.Save.class) FriendLinkRequest request) {
        FriendLink friendLink = request.toDoModel();
        friendLink.setTitle(friendLink.getTitle().trim());
        friendLink.setSort(Integer.valueOf(request.getSort()));
        this.friendLinkService.saveFriendLink(friendLink);
        return Result.success();
    }

    /**
     * 编辑友链
     * @param request
     * @return
     */
    @RequestMapping("edit.json")
    @ResponseBody
    public Result edit(@Validated(BaseRequest.Update.class) FriendLinkRequest request) {
        FriendLink friendLink = request.toDoModel();
        friendLink.setTitle(friendLink.getTitle().trim());
        friendLink.setSort(Integer.valueOf(request.getSort()));
        this.friendLinkService.updateFriendLink(friendLink);
        return Result.success();
    }

    /**
     * 删除分类
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        this.friendLinkService.removeFriendLinkBatch(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(FriendLinkRequest request) {
        PageInfo<FriendLink> pageInfo = this.friendLinkService.findPage(request);
        return Result.success(pageInfo);
    }
}
