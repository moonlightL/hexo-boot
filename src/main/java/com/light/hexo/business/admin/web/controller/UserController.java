package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.UserExtend;
import com.light.hexo.business.admin.service.UserExtendService;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.model.User;
import com.light.hexo.business.admin.service.UserService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.model.UserRequest;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: UserController
 * @ProjectName hexo-boot
 * @Description: 用户控制器
 * @DateTime 2020/9/8 14:58
 */
@RequestMapping("/admin/user")
@Controller
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserExtendService userExtendService;

    /**
     * 个人详情页
     * @param session
     * @param resultMap
     * @return
     */
    @RequestMapping("profileUI.html")
    public String profileUI(HttpSession session, Map<String, Object> resultMap) {
        User user = (User) session.getAttribute(HexoConstant.CURRENT_USER);
        resultMap.put("user", user);
        return this.render("profileUI", resultMap);
    }

    /**
     * 个人介绍
     * @param resultMap
     * @return
     */
    @RequestMapping("aboutUI.html")
    public String aboutUI(HttpSession session, Map<String, Object> resultMap) {
        User user = (User) session.getAttribute(HexoConstant.CURRENT_USER);
        UserExtend userExtend = this.userExtendService.getUserExtendByUid(user.getId());
        resultMap.put("userExtend", userExtend);
        return this.render("aboutUI", resultMap);
    }

    /**
     * 修改信息
     * @param userRequest
     * @param session
     * @return
     */
    @RequestMapping("updateInfo.json")
    @ResponseBody
    public Result updateInfo(@Validated(UserRequest.Update.class)UserRequest userRequest, HttpSession session) {

        User user = (User) session.getAttribute(HexoConstant.CURRENT_USER);
        if (user == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_LOGIN_EXPIRE);
        }

        User tmp = new User();
        tmp.setId(user.getId());
        tmp.setAvatar(userRequest.getAvatar());
        tmp.setNickname(userRequest.getNickname());
        tmp.setEmail(userRequest.getEmail());
        this.userService.updateInfo(tmp);

        user.setAvatar(userRequest.getAvatar());
        user.setNickname(userRequest.getNickname());
        user.setEmail(userRequest.getEmail());

        return Result.success();
    }

    /**
     * 修改密码
     * @param userRequest
     * @return
     */
    @RequestMapping("updatePassword.json")
    @ResponseBody
    public Result updatePassword(@Validated(UserRequest.UpdatePwd.class) UserRequest userRequest, HttpSession session) {

        User user = (User) session.getAttribute(HexoConstant.CURRENT_USER);
        if (user == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_LOGIN_EXPIRE);
        }

        if (!DigestUtils.md5DigestAsHex(userRequest.getOldPassword().getBytes()).equals(user.getPassword())) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_WRONG_PASSWORD_UPDATE);
        }

        User tmp = new User();
        tmp.setId(user.getId());
        String encodePassword = DigestUtils.md5DigestAsHex(userRequest.getPassword().getBytes());
        tmp.setPassword(encodePassword);
        this.userService.updateModel(tmp);

        return Result.success();
    }

    /**
     * 修改状态
     * @param request
     * @return
     */
    @RequestMapping("updateState.json")
    @ResponseBody
    public Result updateState(UserRequest request) {
        this.userService.updateState(request.toDoModel());
        return Result.success();
    }

    /**
     * 删除用户
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    public Result remove(@RequestParam String idStr, HttpSession session) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        User user = (User) session.getAttribute(HexoConstant.CURRENT_USER);
        this.userService.removeUserBatch(Arrays.asList(idStr.split(",")), user.getId());
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @GetMapping("/list.json")
    @ResponseBody
    public Result list(UserRequest request) throws GlobalException {
        PageInfo<User> pageInfo = this.userService.findPage(request);
        return Result.success(pageInfo);
    }

    /**
     * 保存
     * @param descr
     * @return
     */
    @RequestMapping("saveUserExtend.json")
    @ResponseBody
    public Result saveUserExtend(@RequestParam String descr, HttpSession session) {
        User user = (User) session.getAttribute(HexoConstant.CURRENT_USER);
        this.userExtendService.saveUserExtend(user.getId(), descr);
        return Result.success();
    }
}
