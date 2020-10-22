package com.light.hexo.business.portal.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.GuestBook;
import com.light.hexo.business.admin.model.User;
import com.light.hexo.business.portal.common.CommonController;
import com.light.hexo.business.portal.component.RequestLimit;
import com.light.hexo.business.portal.util.ThreadUtil;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.GuestBookRequest;
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
 * @ClassName: IndexGuestBookController
 * @ProjectName hexo-boot
 * @Description: 留言板控制器（首页）
 * @DateTime 2020/9/21 17:16
 */
@Controller
public class IndexGuestBookController extends CommonController {

    /**
     * 留言板列表
     * @param pageNum
     * @return
     * @throws GlobalException
     */
    @GetMapping("/guestBookList/{pageNum}")
    @ResponseBody
    public Result getGuestBookList(@PathVariable Integer pageNum) throws GlobalException {
        List<GuestBook> list = this.guestBookService.listGuestBookByIndex(pageNum, PAGE_SIZE);
        return Result.success(new PageInfo<>(list));
    }

    @GetMapping("/guestBookList.json")
    @ResponseBody
    public Result guestBookList(@RequestParam(defaultValue = "1") Integer pageNum) throws GlobalException {
        List<GuestBook> list = this.guestBookService.listGuestBookByIndex(pageNum, PAGE_SIZE);
        return Result.success(new PageInfo<>(list));
    }

    /**
     * 留言板 发言
     * @param request
     * @param httpServletRequest
     * @return
     * @throws GlobalException
     */
    @PostMapping("/auth/sendGuestBook.json")
    @ResponseBody
    @RequestLimit(cacheName = "guestBookCache", time = 60, msg = "留言次数过于频繁，请等待60秒后再评论")
    public Result sendGuestBook(@Validated(GuestBookRequest.Send.class) GuestBookRequest request, HttpServletRequest httpServletRequest) throws GlobalException {

        GuestBook guestBook = request.toDoModel();
        String ipAddr = IpUtil.getIpAddr(httpServletRequest);
        guestBook.setIpAddress(ipAddr);
        guestBook.setBrowser(BrowserUtil.getBrowserName(httpServletRequest));
        this.guestBookService.saveGuestBookByIndex(guestBook);
        return Result.success();
    }

}
