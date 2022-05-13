package com.light.hexo.core.admin.web.controller;

import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.constant.HexoExceptionEnum;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.request.UserRequest;
import com.light.hexo.common.util.CacheUtil;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.RequestUtil;
import com.light.hexo.common.vo.Result;
import com.light.hexo.core.admin.service.BlacklistService;
import com.light.hexo.core.admin.service.UserService;
import com.light.hexo.mapper.model.User;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: LoginController
 * @ProjectName hexo-boot
 * @Description: 登录控制器
 * @DateTime 2020/7/30 14:26
 */
@RequestMapping("/admin")
@Controller
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlacklistService blacklistService;

    /**
     * 获取验证码
     * @param response
     * @param session
     * @throws Exception
     */
    @RequestMapping(value = "/captcha.jpg")
    public void getCaptchaImage(HttpServletResponse response, HttpSession session) throws Exception {
        // 设置请求头为输出图片类型
        CaptchaUtil.setHeader(response);
        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 32, 4);
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_NUM_AND_UPPER);
        // 验证码存入 session
        session.setAttribute(HexoConstant.CAPTCHA, specCaptcha.text().toLowerCase());
        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }

    /**
     * 登录页
     * @param resultMap
     * @return
     */
    @RequestMapping("/login.html")
    public String loginUI(Map<String, Object> resultMap) {
        return super.render("login", resultMap);
    }

    /**
     * 登录
     * @param request
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/login.json")
    @ResponseBody
    @OperateLog(value = "系统登录", actionType = ActionEnum.LOGIN)
    public Result login(@Validated(UserRequest.Login.class) UserRequest request, HttpServletRequest httpServletRequest) {

        HttpSession session = httpServletRequest.getSession();
        String capText = (String) session.getAttribute(HexoConstant.CAPTCHA);
        if (!request.getVerifyCode().equalsIgnoreCase(capText)) {
            int remainNum = this.checkLoginError(httpServletRequest);
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_VERIFY_CODE_WRONG.getCode(),
                    GlobalExceptionEnum.ERROR_VERIFY_CODE_WRONG.getMessage() + ", 剩余 " + remainNum + " 次尝试机会");
        }

        session.removeAttribute(HexoConstant.CAPTCHA);

        User user = this.userService.findUserByUsername(request.getUsername().trim());
        if (user == null) {
            int remainNum = this.checkLoginError(httpServletRequest);
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_USER_NOT_EXIST.getCode(),
                    HexoExceptionEnum.ERROR_USER_NOT_EXIST.getMessage() + ", 剩余 " + remainNum + " 次尝试机会");
        }

        if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(request.getPassword().trim().getBytes()))) {
            int remainNum = this.checkLoginError(httpServletRequest);
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_USER_PASSWORD_WRONG.getCode(),
                    HexoExceptionEnum.ERROR_USER_PASSWORD_WRONG.getMessage() + ", 剩余 " + remainNum + " 次尝试机会");
        }

        if (!user.getRole().equals(1)) {
            int remainNum = this.checkLoginError(httpServletRequest);
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_STATE_WRONG.getCode(),
                    HexoExceptionEnum.ERROR_STATE_WRONG.getMessage() + ", 剩余 " + remainNum + " 次尝试机会");
        }

        if (!user.getState()) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_STATE_WRONG);
        }

        session.setAttribute(HexoConstant.CURRENT_USER, user);

        String ipAddr = RequestUtil.getIpAddr(httpServletRequest);
        CacheUtil.remove(CacheKey.LOGIN_ERROR_NUM + ":" + ipAddr);

        return Result.success("/admin/home/index.html");
    }

    /**
     * 注销
     * @param session
     * @return
     */
    @RequestMapping("/logout.json")
    @ResponseBody
    public Result logout(HttpSession session) {
        session.removeAttribute(HexoConstant.CURRENT_USER);
        session.invalidate();
        return Result.success();
    }

    private Integer checkLoginError(HttpServletRequest httpServletRequest) {
        String ipAddr = RequestUtil.getIpAddr(httpServletRequest);
        Integer errorNum = CacheUtil.incr(CacheKey.LOGIN_ERROR_NUM + ":" + ipAddr);
        if (errorNum >= HexoConstant.CHANGE_NUM) {
            this.blacklistService.saveBlacklist(ipAddr, "频繁登录失败", 2);
            CacheUtil.remove(CacheKey.LOGIN_ERROR_NUM + ":" + ipAddr);
        }
        return HexoConstant.CHANGE_NUM - errorNum;
    }
}
