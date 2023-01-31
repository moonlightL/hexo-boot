package com.light.hexo.core.portal.web.controller;

import com.light.hexo.common.vo.Result;
import com.light.hexo.core.portal.common.CommonController;
import com.light.hexo.core.portal.model.HexoPageInfo;
import com.light.hexo.mapper.model.Dynamic;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
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
    @GetMapping(value = {"archives", "archives/", "archives/index.html", "archives/page/{pageNum}/"})
    public String archives(@PathVariable(value="pageNum", required = false) Integer pageNum, Map<String, Object> resultMap) {
        pageNum = pageNum == null ? 1 : pageNum;
        HexoPageInfo pageInfo =  this.postService.archivePostsByIndex(pageNum, PAGE_SIZE);
        resultMap.put("pageInfo", pageInfo);
        resultMap.put("currentNav", this.navService.findByLink("/archives/"));
        return render("archives", false, resultMap);
    }

    /**
     * 加载归档
     * @param pageNum
     * @return
     */
    @PostMapping("loadArchive/{pageNum}")
    @ResponseBody
    public Result loadArchive(@PathVariable Integer pageNum) {
        return Result.success(this.postService.archivePostsByIndex(pageNum, PAGE_SIZE));
    }
}
