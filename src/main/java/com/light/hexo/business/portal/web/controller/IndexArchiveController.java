package com.light.hexo.business.portal.web.controller;

import com.light.hexo.business.admin.model.Nav;
import com.light.hexo.business.admin.model.Post;
import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.model.event.NavEvent;
import com.light.hexo.business.portal.common.CommonController;
import com.light.hexo.business.portal.model.HexoPageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        Nav nav = this.navService.findByLink("/archives/");
        resultMap.put("currentNav", nav);
        this.eventPublisher.emit(new NavEvent(nav.getId(), NavEvent.Type.READ));
        return render("archives", false, resultMap);
    }
}
