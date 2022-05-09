package com.light.hexo.core.portal.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.common.util.RequestUtil;
import com.light.hexo.common.vo.Result;
import com.light.hexo.core.portal.common.CommonController;
import com.light.hexo.mapper.model.Dynamic;
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
        resultMap.put("currentNav", this.navService.findByLink("/dynamics/"));
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
        String ipAddr = RequestUtil.getIpAddr(request);
        int prizeNum = this.dynamicService.praiseDynamic(ipAddr, dynamicId);
        return Result.success(prizeNum);
    }
}
