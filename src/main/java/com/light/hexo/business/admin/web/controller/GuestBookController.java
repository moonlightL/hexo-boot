package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.business.admin.model.GuestBook;
import com.light.hexo.business.admin.model.User;
import com.light.hexo.business.admin.service.GuestBookService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.GuestBookRequest;
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
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: GuestBookController
 * @ProjectName hexo-boot
 * @Description: 留言控制器
 * @DateTime 2020/9/4 14:54
 */
@RequestMapping("/admin/guestBook")
@Controller
public class GuestBookController extends BaseController {

    @Autowired
    private GuestBookService guestBookService;

    /**
     * 留言
     * @param resultMap
     * @return
     */
    @RequestMapping("addUI.html")
    public String addUI(Map<String, Object> resultMap) {
        return this.render("addUI", resultMap);
    }

    /**
     * 留言回复页
     * @param resultMap
     * @return
     */
    @RequestMapping("replyUI.html")
    public String replyUI(Integer id, Map<String, Object> resultMap) {
        GuestBook guestBook = this.guestBookService.findById(id);
        resultMap.put("vo", guestBook);
        resultMap.put("pId", guestBook.getId());
        return this.render("replyUI", resultMap);
    }

    /**
     * 留言
     * @param request
     * @return
     */
    @RequestMapping("add.json")
    @ResponseBody
    public Result add(@Validated(BaseRequest.Save.class) GuestBookRequest request, HttpServletRequest httpServletRequest) {
        GuestBook guestBook = request.toDoModel();
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute(HexoConstant.CURRENT_USER);
        guestBook.setUserId(user.getId());
        guestBook.setNickname(user.getNickname().trim());
        guestBook.setBrowser(BrowserUtil.getBrowserName(httpServletRequest));
        guestBook.setIpAddress(IpUtil.getIpAddr(httpServletRequest));
        this.guestBookService.saveGuestBook(guestBook);
        return Result.success();
    }

    /**
     * 删除留言
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        this.guestBookService.removeGuestBookBatch(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(GuestBookRequest request) {
        PageInfo<GuestBook> pageInfo = this.guestBookService.findPage(request);
        return Result.success(pageInfo);
    }

    /**
     * 回复
     * @param request
     * @return
     */
    @RequestMapping("reply.json")
    @ResponseBody
    public Result reply(@Validated(GuestBookRequest.Reply.class) GuestBookRequest request, HttpServletRequest httpServletRequest) {
        GuestBook guestBook = request.toDoModel();
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute(HexoConstant.CURRENT_USER);
        guestBook.setUserId(user.getId());
        guestBook.setNickname(user.getNickname());
        guestBook.setBrowser(BrowserUtil.getBrowserName(httpServletRequest));
        guestBook.setIpAddress(IpUtil.getIpAddr(httpServletRequest));
        this.guestBookService.replyByAdmin(guestBook);
        return Result.success();
    }

    /**
     * 加入黑名单
     * @param request
     * @return
     */
    @RequestMapping("addBlacklist.json")
    @ResponseBody
    public Result addBlacklist(GuestBookRequest request, HttpServletRequest httpServletRequest) {
        this.guestBookService.addBlacklist(request.toDoModel(), IpUtil.getIpAddr(httpServletRequest));
        return Result.success();
    }
}
