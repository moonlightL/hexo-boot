package com.light.hexo.core.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.request.CommentRequest;
import com.light.hexo.common.util.BrowserUtil;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.RequestUtil;
import com.light.hexo.common.vo.Result;
import com.light.hexo.core.admin.service.CommentService;
import com.light.hexo.mapper.model.Comment;
import com.light.hexo.mapper.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: CommentController
 * @ProjectName hexo-boot
 * @Description: 评论控制器
 * @DateTime 2022/2/15, 0015 18:03
 */
@RequestMapping("/admin/comment")
@Controller
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    /**
     * 评论回复页
     * @param resultMap
     * @return
     */
    @RequestMapping("replyUI.html")
    public String replyUI(Integer id, Map<String, Object> resultMap) {
        Comment comment = this.commentService.findById(id);
        resultMap.put("vo", comment);
        resultMap.put("pId", comment.getId());
        return this.render("replyUI", resultMap);
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

        this.commentService.removeCommentBatch(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(CommentRequest request) {
        PageInfo<Comment> pageInfo = this.commentService.findPage(request);
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
    public Result reply(@Validated(CommentRequest.Reply.class) CommentRequest request, HttpServletRequest httpServletRequest) {
        Comment comment = request.toDoModel();
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute(HexoConstant.CURRENT_USER);
        comment.setNickname(user.getNickname());
        comment.setAvatar(user.getAvatar());
        comment.setEmail(user.getEmail());
        comment.setOsName(BrowserUtil.getOsName(httpServletRequest));
        comment.setBrowser(BrowserUtil.getBrowserName(httpServletRequest));
        comment.setIpAddress(RequestUtil.getIpAddr(httpServletRequest));
        this.commentService.replyByAdmin(comment);
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
    public Result addBlacklist(CommentRequest request, HttpServletRequest httpServletRequest) {
        this.commentService.addBlacklist(request.toDoModel(), RequestUtil.getIpAddr(httpServletRequest));
        return Result.success();
    }
}
