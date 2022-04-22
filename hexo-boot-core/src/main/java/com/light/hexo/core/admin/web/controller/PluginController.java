package com.light.hexo.core.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.request.PluginRequest;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.vo.Result;
import com.light.hexo.core.admin.service.SysPluginService;
import com.light.hexo.mapper.model.SysPlugin;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: PluginController
 * @ProjectName hexo-boot
 * @Description: 插件控制器
 * @DateTime 2022/4/13, 0013 10:31
 */
@RequestMapping("/admin/plugin")
@Controller
public class PluginController extends BaseController {

    @Autowired
    private SysPluginService pluginService;

    /**
     * 修改状态
     * @param request
     * @return
     */
    @RequestMapping("updateState.json")
    @ResponseBody
    @OperateLog(value = "修改插件状态", actionType = ActionEnum.ADMIN_EDIT)
    public Result updateState(PluginRequest request) {
        this.pluginService.updatePlugin(request.toDoModel());
        return Result.success();
    }


    /**
     * 解压插件
     * @param resultMap
     * @return
     */
    @GetMapping("/unzipUI.html")
    public String unzipUI(Map<String,Object> resultMap) {
        return render("unzipUI", resultMap);
    }


    @RequestMapping("/unzipPlugin.json")
    @ResponseBody
    @OperateLog(value = "按钮插件", actionType = ActionEnum.ADMIN_ADD)
    public Result unzipTheme(MultipartFile file) {

        if (file == null) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        String contentType = file.getContentType();
        if (!"application/x-zip-compressed".equalsIgnoreCase(contentType) && !"application/zip".equalsIgnoreCase(contentType)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        try {
            String originalFilename = file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();
            this.pluginService.unzipPlugin(originalFilename, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return Result.success();
    }

    /**
     * 卸载插件
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    @OperateLog(value = "卸载插件", actionType = ActionEnum.ADMIN_REMOVE)
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        String[] split = idStr.split(",");
        this.pluginService.removePlugin(Integer.valueOf(split[0]));
        return Result.success();
    }


    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(PluginRequest request) {
        PageInfo<SysPlugin> pageInfo = this.pluginService.findPage(request);
        return Result.success(pageInfo);
    }

    /**
     * 用于测试
     * @param map
     * @return
     */
    @RequestMapping("testUI.html")
    public String testUI(Map<String, Object> map) {
        return this.render("testUI", map);
    }
}
