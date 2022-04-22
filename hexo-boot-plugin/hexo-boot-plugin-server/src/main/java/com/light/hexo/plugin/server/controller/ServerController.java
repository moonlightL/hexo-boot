package com.light.hexo.plugin.server.controller;

import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.vo.Result;
import com.light.hexo.plugin.server.model.ServerInfo;
import com.light.hexo.plugin.server.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: ServerController
 * @ProjectName hexo-boot
 * @Description: 服务器信息
 * @DateTime 2022/4/12, 0012 17:38
 */
@RequestMapping("/plugin/server")
@Controller
public class ServerController extends BaseController {

    @Autowired
    private ServerService serverService;

    /**
     * 获取服务器信息
     * @return
     */
    @RequestMapping("getServerData.json")
    @ResponseBody
    public Result getServerData() {
        ServerInfo serverInfo = this.serverService.getServerInfo();
        serverInfo.collectInfo();
        return Result.success(serverInfo);
    }

    /**
     * 配置页面
     * @param map
     * @return
     */
    @RequestMapping("configUI.html")
    public String configUI(Map<String, Object> map) {
        return this.render("configUI", map);
    }
}
