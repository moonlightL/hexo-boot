package com.light.hexo.core.portal.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.request.CommentRequest;
import com.light.hexo.common.util.BrowserUtil;
import com.light.hexo.common.util.HttpClientUtil;
import com.light.hexo.common.util.JsonUtil;
import com.light.hexo.common.util.RequestUtil;
import com.light.hexo.common.vo.Result;
import com.light.hexo.core.portal.common.CommonController;
import com.light.hexo.core.portal.component.RequestLimit;
import com.light.hexo.core.portal.model.BasicInfo;
import com.light.hexo.mapper.model.Comment;
import com.light.hexo.mapper.model.Theme;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: IndexCommentController
 * @ProjectName hexo-boot
 * @Description: 评论控制器（首页）
 * @DateTime 2020/9/21 16:22
 */
@Controller
public class IndexCommentController extends CommonController {

    /**
     * 文章评论列表
     * @param page
     * @param pageNum
     * @return
     * @throws GlobalException
     */
    @GetMapping("commentList.json")
    @ResponseBody
    public Result commentList(@RequestParam(defaultValue = "/") String page, @RequestParam(defaultValue = "1") Integer pageNum) throws GlobalException {

        Theme activeTheme = this.themeService.getActiveTheme(true);
        String commentShowType = activeTheme.getConfigMap().get("commentShowType");
        Map<String, Object> map = new HashMap<>();
        List<Comment> commentList = this.commentService.listCommentByPage(page, pageNum, PAGE_SIZE, "singleRow".equals(commentShowType));
        PageInfo<Comment> pageInfo = new PageInfo<>(commentList);
        map.put("totalNum", "singleRow".equals(commentShowType) ? pageInfo.getTotal() : this.commentService.getCommentNumByBannerId(page));
        map.put("commentList", pageInfo.getList());
        map.put("commentShowType", commentShowType);
        return Result.success(map);
    }

    /**
     * 文章评论
     * @param request
     * @param httpServletRequest
     * @return
     * @throws GlobalException
     */
    @PostMapping("auth/sendComment.json")
    @ResponseBody
    @RequestLimit(cacheName = "commentCache")
    public Result sendComment(@Validated(CommentRequest.Send.class) CommentRequest request, HttpServletRequest httpServletRequest) throws GlobalException {

        Comment comment = request.toDoModel();
        String ipAddr = RequestUtil.getIpAddr(httpServletRequest);
        comment.setIpAddress(ipAddr);
        comment.setOsName(BrowserUtil.getOsName(httpServletRequest));
        comment.setBrowser(BrowserUtil.getBrowserName(httpServletRequest));
        this.commentService.saveCommentByIndex(comment);
        return Result.success();
    }


    private static final String QQ_INFO_URL = "https://api.usuuu.com/qq/%s";

    @GetMapping("/getQQInfo/{qq}")
    @ResponseBody
    public Result getQQInfo(@PathVariable("qq") String qq) {
        String url = String.format(QQ_INFO_URL, qq);
        String content = HttpClientUtil.sendGet(url);
        Map<String, Object> map = JsonUtil.string2Obj(content, Map.class);
        Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
        BasicInfo basicInfo = new BasicInfo(dataMap.get("name").toString(), dataMap.get("avatar").toString());
        return Result.success(basicInfo);
    }

}
