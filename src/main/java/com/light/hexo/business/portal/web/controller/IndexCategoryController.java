package com.light.hexo.business.portal.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.Category;
import com.light.hexo.business.admin.model.Post;
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
    @GetMapping(value = {"/categories", "/categories/", "/categories/index.html"})
    public String categories(Map<String, Object> resultMap) {
        List<Category> categoryList = this.categoryService.listCategoriesByIndex();
        resultMap.put("categoryList", categoryList);
        resultMap.put("categoryNum", categoryList.size());
        resultMap.put("menu", "categories");
        return render("categories", false, resultMap);
    }

    @GetMapping(value = "/categories/{categoryName}/")
    public String categoriesByName(@PathVariable String categoryName, Map<String, Object> resultMap) {
        List<Post> postList = this.postService.listPostsByCategoryName(categoryName, 1, PAGE_SIZE);
        resultMap.put("pageInfo", new PageInfo<>(postList, PAGE_SIZE));
        resultMap.put("name", categoryName);
        resultMap.put("type", "分类");
        return render("postList", false, resultMap);
    }

    @GetMapping("/categories/{categoryName}/page/{pageNum}/")
    public String categoriesPage(@PathVariable String categoryName, @PathVariable Integer pageNum, Map<String, Object> resultMap) {
        List<Post> postList = this.postService.listPostsByCategoryName(categoryName, pageNum, PAGE_SIZE);
        resultMap.put("pageInfo", new PageInfo<>(postList, PAGE_SIZE));
        resultMap.put("name", categoryName);
        resultMap.put("type", "分类");
        return render("postList", false, resultMap);
    }
}
