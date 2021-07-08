package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.business.admin.model.Post;
import com.light.hexo.business.admin.model.PostComment;
import com.light.hexo.business.admin.model.User;
import com.light.hexo.business.admin.service.PostCommentService;
import com.light.hexo.business.admin.service.PostService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.PostCommentRequest;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.BrowserUtil;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: PostCommentController
 * @ProjectName hexo-boot
 * @Description: 文章评论控制器
 * @DateTime 2020/8/21 12:01
 */
@RequestMapping("/admin/postComment")
@Controller
public class PostCommentController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostCommentService postCommentService;

    /**
     * 评论页
     * @param resultMap
     * @return
     */
    @RequestMapping("addUI.html")
    public String addUI(Map<String, Object> resultMap) {
        List<Post> list = this.postService.findAll();
        resultMap.put("postList", list);
        return this.render("addUI", resultMap);
    }

    /**
     * 评论回复页
     * @param resultMap
     * @return
     */
    @RequestMapping("replyUI.html")
    public String replyUI(Integer id, Map<String, Object> resultMap) {
        PostComment postComment = this.postCommentService.findById(id);
        resultMap.put("vo", postComment);
        resultMap.put("pId", postComment.getId());
        return this.render("replyUI", resultMap);
    }

    /**
     * 评论
     * @param request
     * @return
     */
    @RequestMapping("add.json")
    @ResponseBody
    @OperateLog(value = "后台评论", actionType = ActionEnum.ADMIN_ADD)
    public Result add(@Validated(BaseRequest.Save.class) PostCommentRequest request, HttpServletRequest httpServletRequest) {
        PostComment postComment = request.toDoModel();
        User user = (User) httpServletRequest.getSession().getAttribute(HexoConstant.CURRENT_USER);
        postComment.setUserId(user.getId());
        postComment.setNickname(user.getNickname());
        postComment.setBrowser(BrowserUtil.getBrowserName(httpServletRequest));
        postComment.setIpAddress(IpUtil.getIpAddr(httpServletRequest));
        this.postCommentService.savePostComment(postComment);
        return Result.success();
    }

    /**
     * 删除评论
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    @OperateLog(value = "删除评论", actionType = ActionEnum.ADMIN_REMOVE)
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        this.postCommentService.removePostCommentBatch(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(PostCommentRequest request) {
        PageInfo<PostComment> pageInfo = this.postCommentService.findPage(request);
        return Result.success(pageInfo);
    }

    /**
     * 回复评论
     * @param request
     * @return
     */
    @RequestMapping("reply.json")
    @ResponseBody
    @OperateLog(value = "后台评论回复", actionType = ActionEnum.ADMIN_ADD)
    public Result reply(@Validated(PostCommentRequest.Reply.class) PostCommentRequest request, HttpServletRequest httpServletRequest) {
        PostComment postComment = request.toDoModel();
        User user = (User) httpServletRequest.getSession().getAttribute(HexoConstant.CURRENT_USER);
        postComment.setUserId(user.getId());
        postComment.setNickname(user.getNickname().trim());
        postComment.setBrowser(BrowserUtil.getBrowserName(httpServletRequest));
        postComment.setIpAddress(IpUtil.getIpAddr(httpServletRequest));
        this.postCommentService.replyByAdmin(postComment);
        return Result.success();
    }

    /**
     * 加入黑名单
     * @param request
     * @return
     */
    @RequestMapping("addBlacklist.json")
    @ResponseBody
    @OperateLog(value = "评论-加入黑名单", actionType = ActionEnum.ADMIN_ADD)
    public Result addBlacklist(PostCommentRequest request, HttpServletRequest httpServletRequest) {
        this.postCommentService.addBlacklist(request.toDoModel(), IpUtil.getIpAddr(httpServletRequest));
        return Result.success();
    }
}
