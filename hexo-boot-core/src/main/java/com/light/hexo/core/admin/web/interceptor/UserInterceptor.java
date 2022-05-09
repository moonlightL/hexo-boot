package com.light.hexo.core.admin.web.interceptor;

import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.vo.Result;
import com.light.hexo.common.constant.HexoExceptionEnum;
import com.light.hexo.mapper.model.User;
import com.light.hexo.common.util.RequestUtil;
import com.light.hexo.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author MoonlightL
 * @ClassName: UserInterceptor
 * @ProjectName hexo-boot
 * @Description: 用户拦截器
 * @DateTime 2020/9/9 16:06
 */
@Component
@Slf4j
public class UserInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Object obj = request.getSession().getAttribute(HexoConstant.CURRENT_USER);
        if (obj == null) {
            if (RequestUtil.isAjax(request)) {
                this.print(response, JsonUtil.obj2String(Result.fail(HexoExceptionEnum.ERROR_LOGIN_EXPIRE)));
            } else {
                response.sendRedirect("/admin/login.html");
            }
            return false;
        }

        User user = (User) obj;
        // 后台接口请求操作用户身份只能是管理员/博主
        if (!user.getRole().equals(1)) {
            if (RequestUtil.isAjax(request)) {
                this.print(response, JsonUtil.obj2String(Result.fail(HexoExceptionEnum.ERROR_NOT_PERMISSION_OPERATE)));
            } else {
                response.sendRedirect("/admin/login.html");
            }
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }

    private void print(HttpServletResponse response, String result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(result.getBytes(StandardCharsets.UTF_8));
    }
}
