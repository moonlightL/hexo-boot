package com.light.hexo.business.admin.web.controller;

import com.light.hexo.business.admin.component.InstallService;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.model.InstallRequest;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.model.UserRequest;
import com.light.hexo.common.util.BrowserUtil;
import com.light.hexo.common.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: InstallController
 * @ProjectName hexo-boot
 * @Description: 安装控制器
 * @DateTime 2020/9/11 17:29
 */
@RequestMapping("/admin")
@Controller
public class InstallController {

    @Autowired
    private InstallService installService;

    @Autowired
    private Environment environment;

    /**
     * 安装页
     * @param resultMap
     * @return
     */
    @RequestMapping("/install.html")
    public String install(Map<String, Object> resultMap) {
        resultMap.put("webSite", "http://" + IpUtil.getHostIp() + ":" + this.environment.getProperty("server.port"));
        // 此处开头不能加 "/"，否则 jar 方式运行访问会报错
        return "admin/install";
    }

    /**
     * 安装系统
     * @param installRequest
     * @return
     */
    @RequestMapping("/install.json")
    @ResponseBody
    @OperateLog(value = "安装博客系统", actionType = ActionEnum.INSTALL)
    public Result install(@Validated(BaseRequest.Save.class) InstallRequest installRequest, HttpServletRequest httpServletRequest) throws Exception{
        this.installService.installApplication(
                installRequest,
                BrowserUtil.getOsName(httpServletRequest),
                BrowserUtil.getBrowserName(httpServletRequest),
                IpUtil.getIpAddr(httpServletRequest));
        return Result.success("/admin/login.html");
    }
}
