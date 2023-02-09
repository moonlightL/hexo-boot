package com.light.hexo.core.portal.web.controller;

import com.light.hexo.core.portal.common.CommonController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: IndexAboutController
 * @ProjectName hexo-boot
 * @Description: 关于控制器
 * @DateTime 2023/2/2, 0002 15:09
 */
@Controller
public class IndexAboutController extends CommonController {

    /**
     * 关于
     * @param resultMap
     * @return
     */
    @GetMapping(value = "about/")
    public String about(Map<String, Object> resultMap) {
        resultMap.put("about", this.userExtendService.getBloggerInfo());
        return render("about", false, resultMap);
    }
}
