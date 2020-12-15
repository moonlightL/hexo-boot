package com.light.hexo.business.portal.web.controller;

import com.light.hexo.business.portal.common.CommonController;
import com.light.hexo.business.portal.model.HexoPageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: IndexArchiveController
 * @ProjectName hexo-boot
 * @Description: 归档控制器（首页）
 * @DateTime 2020/9/21 11:11
 */
@Controller
public class IndexArchiveController extends CommonController {

    /**
     * 归档
     * @param resultMap
     * @return
     */
    @GetMapping(value = {"archives", "archives/", "archives/index.html"})
    public String archives(Map<String, Object> resultMap) {
        HexoPageInfo pageInfo =  this.postService.archivePostsByIndex(1, PAGE_SIZE);
        resultMap.put("pageInfo", pageInfo);
        resultMap.put("menu", "archives");
        return render("archives", false, resultMap);
    }

    @GetMapping(value = "archives/page/{pageNum}/")
    public String archivesPage(@PathVariable("pageNum") Integer pageNum, Map<String, Object> resultMap) {
        HexoPageInfo pageInfo =  this.postService.archivePostsByIndex(pageNum, PAGE_SIZE);
        resultMap.put("pageInfo", pageInfo);
        resultMap.put("menu", "archives");
        return render("archives", false, resultMap);
    }
}
