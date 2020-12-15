package com.light.hexo.business.portal.web.controller;

import com.light.hexo.business.admin.model.Nav;
import com.light.hexo.business.admin.service.NavService;
import com.light.hexo.business.portal.common.CommonController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: IndexCustomController
 * @ProjectName hexo-boot
 * @Description: 自定义页面控制器
 * @DateTime 2020/12/14 17:25
 */
@Controller
public class IndexCustomController extends CommonController {

    @Autowired
    private NavService navService;

    /**
     * 自定义页面导航
     * @param link
     * @param resultMap
     * @return
     */
    @RequestMapping("/custom/{link}")
    public String page(@PathVariable String link, Map<String, Object> resultMap) {
        Nav nav = this.navService.findByLink(link);
        resultMap.put("nav", nav);
        resultMap.put("menu", nav.getCode());
        return render("custom", true, resultMap);
    }

}
