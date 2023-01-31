package com.light.hexo.core.portal.web.controller;

import com.light.hexo.core.portal.common.CommonController;
import com.light.hexo.mapper.model.Post;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: IndexSearchController
 * @ProjectName hexo-boot
 * @Description: 搜索控制器
 * @DateTime 2023/1/30, 0030 17:20
 */
@Controller
public class IndexSearchController extends CommonController {

    /**
     * 搜索页面
     * @param resultMap
     * @return
     */
    @GetMapping("searchPage/")
    public String searchPage(Map<String, Object> resultMap) {
        List<Post> list = new ArrayList<>();
        resultMap.put("result", list);
        return render("search", false, resultMap);
    }

    /**
     * 关键字搜索
     * @param keyword
     * @param resultMap
     * @return
     */
    @GetMapping("searchByKeyword/")
    public String searchByKeyword(String keyword, Map<String, Object> resultMap) {
        List<Post> list = this.postService.listPostByKeyword(keyword);
        resultMap.put("result", list);
        resultMap.put("keyword", keyword);
        return render("search", false, resultMap);
    }

    /**
     * 搜索框
     * @param resultMap
     * @return
     */
    @GetMapping(value = "search/")
    public String search(Map<String, Object> resultMap) {
        List<Post> postList = this.postService.listPostByIdList(null);
        resultMap.put("postList", postList);
        return render("search", false, resultMap);
    }
}
