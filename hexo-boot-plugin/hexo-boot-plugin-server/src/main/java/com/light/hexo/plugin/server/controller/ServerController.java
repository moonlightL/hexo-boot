package com.light.hexo.plugin.server.controller;

import com.light.hexo.common.vo.Result;
import com.light.hexo.plugin.server.model.ServerInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author MoonlightL
 * @ClassName: ServerController
 * @ProjectName hexo-boot
 * @Description: 服务器信息
 * @DateTime 2022/4/12, 0012 17:38
 */
@RequestMapping("/plugin/server")
@Controller
public class ServerController {

    /**
     * 获取服务器信息
     * @return
     */
    @RequestMapping("/getServerData.json")
    @ResponseBody
    public Result getServerData() {
        ServerInfo server = new ServerInfo();
        server.collectInfo();
        return Result.success(server);
    }
}
