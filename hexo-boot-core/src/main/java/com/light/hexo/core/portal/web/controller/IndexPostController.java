package com.light.hexo.core.portal.web.controller;

import com.light.hexo.common.vo.Result;
import com.light.hexo.mapper.model.Nav;
import com.light.hexo.mapper.model.Post;
import com.light.hexo.common.event.PostEvent;
import com.light.hexo.core.portal.common.CommonController;
import com.light.hexo.common.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: IndexPostController
 * @ProjectName hexo-boot
 * @Description: 文章控制器（首页）
 * @DateTime 2021/6/29 11:28
 */
@Controller
public class IndexPostController extends CommonController {

    private static final String POST_AUTH_PAGE = "portal/post/auth.html";

    /**
     * 文章内容，URL 的配置格式是为了兼容 hexo
     * @param year
     * @param month
     * @param day
     * @param title
     * @param resultMap
     * @return
     */
    @RequestMapping("{year}/{month}/{day}/{title}/")
    public String post(@PathVariable("year") String year,
                       @PathVariable("month") String month,
                       @PathVariable("day") String day,
                       @PathVariable("title") String title,
                       String authCode,
                       Map<String, Object> resultMap) {
        String link = year + "/" + month + "/" + day + "/" + title + "/";
        Post post = this.postService.getDetailInfo(link, 1);
        if (StringUtils.isNotBlank(post.getAuthCode())) {
            if (StringUtils.isBlank(authCode)) {
                resultMap.put("title", post.getTitle());
                resultMap.put("link", "/" + link);
                return POST_AUTH_PAGE;
            }

            if (!post.getAuthCode().equals(authCode)) {
                resultMap.put("title", post.getTitle());
                resultMap.put("link", "/" + link);
                resultMap.put("errorMsg", "访问密码不正确");
                return POST_AUTH_PAGE;
            }
        }
        resultMap.put("post", post);
        resultMap.put("previousPost", post.getPrevPost());
        resultMap.put("nextPost", post.getNextPost());
        resultMap.put("currentNav", new Nav(post.getTitle(), post.getLink(), post.getCoverUrl(), "detail"));
        this.eventPublisher.emit(new PostEvent(post.getId(), PostEvent.Type.READ));
        return render("detail", true, resultMap);
    }

    /**
     * 文章内容（自定义链接）
     * @param link
     * @param resultMap
     * @return
     */
    @RequestMapping("{link}.html")
    public String post(@PathVariable("link") String link, String authCode, Map<String, Object> resultMap) throws UnsupportedEncodingException {
        Post post = this.postService.getDetailInfo(link, 2);
        if (StringUtils.isNotBlank(post.getAuthCode())) {
            if (StringUtils.isBlank(authCode)) {
                resultMap.put("title", post.getTitle());
                resultMap.put("link", "/" + link + ".html");
                return POST_AUTH_PAGE;
            }

            if (!post.getAuthCode().equals(authCode)) {
                resultMap.put("title", post.getTitle());
                resultMap.put("link", "/" + link + ".html");
                resultMap.put("errorMsg", "访问密码不正确");
                return POST_AUTH_PAGE;
            }
        }
        resultMap.put("post", post);
        resultMap.put("previousPost", post.getPrevPost());
        resultMap.put("nextPost", post.getNextPost());
        resultMap.put("currentNav", new Nav(post.getTitle(), post.getLink(), post.getCoverUrl(), "detail"));
        this.eventPublisher.emit(new PostEvent(post.getId(), PostEvent.Type.READ));
        return render("detail", true, resultMap);
    }

    /**
     * 点赞文章
     * @param postId
     * @return
     */
    @PostMapping("praisePost/{postId}")
    @ResponseBody
    public Result prizePost(@PathVariable Integer postId, HttpServletRequest request) {
        String ipAddr = IpUtil.getIpAddr(request);
        int prizeNum = this.postService.praisePost(ipAddr, postId);
        return Result.success(prizeNum);
    }
}
