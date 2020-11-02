package com.light.hexo.business.portal.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.GuestBook;
import com.light.hexo.business.admin.model.Theme;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/guestBookList.json")
    @ResponseBody
    public Result guestBookList(@RequestParam(defaultValue = "1") Integer pageNum) throws GlobalException {

        Theme activeTheme = this.themeService.getActiveTheme();
        String commentShowType = activeTheme.getConfigMap().get("commentShowType");
        List<GuestBook> list;
        Map<String, Object> map = new HashMap<>();
        if ("singleRow".equals(commentShowType)) {
            // 单行
            list = this.guestBookService.listGuestBookByIndex(pageNum, PAGE_SIZE);
        } else {
            // 多行（父子级评论一起展示）
            list = this.guestBookService.getGuestBookListByIndex(pageNum, PAGE_SIZE);

        }

        PageInfo<GuestBook> pageInfo = new PageInfo<>(list);
        map.put("totalNum", "singleRow".equals(commentShowType) ? pageInfo.getTotal() : this.guestBookService.getGuestBookNum());
        map.put("commentList", pageInfo.getList());
        map.put("commentShowType", commentShowType);
        return Result.success(map);
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
