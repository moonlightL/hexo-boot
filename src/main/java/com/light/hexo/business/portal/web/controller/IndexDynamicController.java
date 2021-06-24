package com.light.hexo.business.portal.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.Dynamic;
import com.light.hexo.business.admin.model.Nav;
import com.light.hexo.business.admin.model.event.NavEvent;
import com.light.hexo.business.portal.common.CommonController;
import com.light.hexo.business.portal.model.HexoPageInfo;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.IpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: IndexDynamicController
 * @ProjectName hexo-boot
 * @Description: 动态控制器（首页）
 * @DateTime 2021/6/23 18:21
 */
@Controller
public class IndexDynamicController extends CommonController {

    /**
     * 动态
     * @param pageNum
     * @param resultMap
     * @return
     */
    @GetMapping(value = {"dynamics", "dynamics/", "dynamics/index.html", "dynamics/page/{pageNum}/"})
    public String dynamics(@PathVariable(value="pageNum", required = false) Integer pageNum, Map<String, Object> resultMap) {
        pageNum = pageNum == null ? 1 : pageNum;
        List<Dynamic> dynamicList = this.dynamicService.listDynamicByIndex(pageNum, PAGE_SIZE);
        resultMap.put("pageInfo", new PageInfo<>(dynamicList, PAGE_SIZE));
        Nav nav = this.navService.findByLink("/dynamics/");
        resultMap.put("currentNav", nav);
        this.eventPublisher.emit(new NavEvent(nav.getId(), NavEvent.Type.READ));
        return render("dynamics", false, resultMap);
    }

    /**
     * 点赞动态
     * @param dynamicId
     * @return
     */
    @PostMapping("praiseDynamic/{dynamicId}")
    @ResponseBody
    public Result praiseDynamic(@PathVariable Integer dynamicId, HttpServletRequest request) {
        String ipAddr = IpUtil.getIpAddr(request);
        int prizeNum = this.dynamicService.praiseDynamic(ipAddr, dynamicId);
        return Result.success(prizeNum);
    }
}
