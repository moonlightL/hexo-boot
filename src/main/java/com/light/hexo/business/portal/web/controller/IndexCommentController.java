package com.light.hexo.business.portal.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.PostComment;
import com.light.hexo.business.admin.model.User;
import com.light.hexo.business.portal.common.CommonController;
import com.light.hexo.business.portal.component.RequestLimit;
import com.light.hexo.business.portal.util.ThreadUtil;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.PostCommentRequest;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.BrowserUtil;
import com.light.hexo.common.util.IpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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
     * @param postId
     * @param pageNum
     * @return
     * @throws Exception
     */
    @GetMapping("/commentList/{postId}/{pageNum}")
    @ResponseBody
    public Result getCommentList(@PathVariable Integer postId, @PathVariable Integer pageNum) throws GlobalException {
        List<PostComment> commentList = this.postCommentService.listCommentByPostId(postId, pageNum, PAGE_SIZE);
        return Result.success(new PageInfo<>(commentList));
    }

    @GetMapping("/commentList.json")
    @ResponseBody
    public Result commentList(@RequestParam(defaultValue = "0") Integer postId, @RequestParam(defaultValue = "1") Integer pageNum) throws GlobalException {
        List<PostComment> commentList = this.postCommentService.listCommentByPostId(postId, pageNum, PAGE_SIZE);
        return Result.success(new PageInfo<>(commentList));
    }


    /**
     * 文章评论
     * @param request
     * @param httpServletRequest
     * @return
     * @throws GlobalException
     */
    @PostMapping("/auth/sendComment.json")
    @ResponseBody
    @RequestLimit(cacheName = "commentCache", time = 60, msg = "评论次数过于频繁，请等待60秒后再评论")
    public Result sendComment(@Validated(PostCommentRequest.Send.class) PostCommentRequest request, HttpServletRequest httpServletRequest) throws GlobalException {

        PostComment postComment = request.toDoModel();
        String ipAddr = IpUtil.getIpAddr(httpServletRequest);
        postComment.setIpAddress(ipAddr);
        postComment.setBrowser(BrowserUtil.getBrowserName(httpServletRequest));
        this.postCommentService.saveCommentByIndex(postComment);
        return Result.success();
    }

}
