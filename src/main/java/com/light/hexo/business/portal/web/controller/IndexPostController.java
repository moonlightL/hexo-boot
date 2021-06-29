package com.light.hexo.business.portal.web.controller;

import cn.hutool.core.codec.Base64;
import com.light.hexo.business.admin.model.Nav;
import com.light.hexo.business.admin.model.Post;
import com.light.hexo.business.admin.model.event.PostEvent;
import com.light.hexo.business.portal.common.CommonController;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
                return "admin/postAuth.html";
            }

            if (!post.getAuthCode().equals(authCode)) {
                resultMap.put("title", post.getTitle());
                resultMap.put("link", "/" + link);
                resultMap.put("errorMsg", "访问密码不正确");
                return "admin/postAuth.html";
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
    public String post(@PathVariable("link") String link, String authCode, Map<String, Object> resultMap) {
        Post post = this.postService.getDetailInfo(link, 2);
        if (StringUtils.isNotBlank(post.getAuthCode())) {
            if (StringUtils.isBlank(authCode)) {
                resultMap.put("title", post.getTitle());
                resultMap.put("link", "/" + link + ".html");
                return "admin/postAuth.html";
            }

            if (!post.getAuthCode().equals(authCode)) {
                resultMap.put("title", post.getTitle());
                resultMap.put("link", "/" + link + ".html");
                resultMap.put("errorMsg", "访问密码不正确");
                return "admin/postAuth.html";
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
