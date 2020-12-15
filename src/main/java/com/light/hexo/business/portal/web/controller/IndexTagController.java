package com.light.hexo.business.portal.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.Post;
import com.light.hexo.business.admin.model.Tag;
import com.light.hexo.business.portal.common.CommonController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: IndexTagController
 * @ProjectName hexo-boot
 * @Description: 标签控制器（首页）
 * @DateTime 2020/9/19 11:19
 */
@Controller
public class IndexTagController extends CommonController {

    /**
     * 标签
     * @param resultMap
     * @return
     */
    @GetMapping(value = {"tags", "tags/", "tags/index.html"})
    public String tags(Map<String, Object> resultMap) {
        List<Tag> tagList = this.tagService.listTagsByIndex();
        resultMap.put("tagList", tagList);
        resultMap.put("count", tagList.size());
        resultMap.put("menu", "tags");
        return render("tags", false, resultMap);
    }

    @GetMapping(value = "tags/{tagName}/")
    public String tagsByName(@PathVariable String tagName, Map<String, Object> resultMap) {
        List<Post> postList = this.postService.listPostsByTagName(tagName, 1, PAGE_SIZE);
        resultMap.put("pageInfo", new PageInfo<>(postList, PAGE_SIZE));
        resultMap.put("name", tagName);
        resultMap.put("type", "标签");
        resultMap.put("menu", "tags");
        return render("postList", false, resultMap);
    }

    @GetMapping(value = "tags/{tagName}/page/{pageNum}/")
    public String tagsPage(@PathVariable String tagName, @PathVariable Integer pageNum, Map<String, Object> resultMap) {
        List<Post> postList = this.postService.listPostsByTagName(tagName, pageNum, PAGE_SIZE);
        resultMap.put("pageInfo", new PageInfo<>(postList, PAGE_SIZE));
        resultMap.put("name", tagName);
        resultMap.put("type", "标签");
        resultMap.put("menu", "tags");
        return render("postList", false, resultMap);
    }
}
