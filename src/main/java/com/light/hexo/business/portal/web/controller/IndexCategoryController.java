package com.light.hexo.business.portal.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.Category;
import com.light.hexo.business.admin.model.Nav;
import com.light.hexo.business.admin.model.Post;
import com.light.hexo.business.admin.model.event.NavEvent;
import com.light.hexo.business.portal.common.CommonController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: IndexCategoryController
 * @ProjectName hexo-boot
 * @Description: 分类控制器（首页）
 * @DateTime 2020/9/19 11:06
 */
@Controller
public class IndexCategoryController extends CommonController {

    /**
     * 分类
     * @param resultMap
     * @return
     */
    @GetMapping(value = {"categories", "/categories/", "/categories/index.html"})
    public String categories(Map<String, Object> resultMap) {
        List<Category> categoryList = this.categoryService.listCategoriesByIndex();
        resultMap.put("categoryList", categoryList);
        resultMap.put("categoryNum", categoryList.size());
        Nav nav = this.navService.findByLink("/categories/");
        resultMap.put("currentNav", nav);
        this.eventPublisher.emit(new NavEvent(nav.getId(), NavEvent.Type.READ));
        return render("categories", false, resultMap);
    }

    @GetMapping(value = {"categories/{categoryName}/", "categories/{categoryName}/page/{pageNum}/"})
    public String categoriesByName(@PathVariable String categoryName, @PathVariable(value="pageNum", required = false) Integer pageNum, Map<String, Object> resultMap) {
        pageNum = pageNum == null ? 1 : pageNum;
        List<Post> postList = this.postService.listPostsByCategoryName(categoryName, pageNum, PAGE_SIZE);
        resultMap.put("pageInfo", new PageInfo<>(postList, PAGE_SIZE));
        resultMap.put("name", categoryName);
        resultMap.put("type", "分类");
        resultMap.put("link", "categories");
        resultMap.put("currentNav", this.navService.findByLink("/categories/"));
        return render("postList", false, resultMap);
    }
}
