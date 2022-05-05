package com.light.hexo.plugin.server.interceptor;

import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.plugin.annotation.HexoBootIntercept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author MoonlightL
 * @ClassName: ServerInterceptor
 * @ProjectName hexo-boot
 * @Description: 拦截器
 * @DateTime 2022/4/24, 0024 11:13
 */
@HexoBootIntercept({"/plugin/server/**"})
@Slf4j
public class ServerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object obj = request.getSession().getAttribute(HexoConstant.CURRENT_USER);
        if (obj == null) {
            log.info("==================非法请求 server-plugin=====================");
            response.sendRedirect("/");
            return false;
        }
        return true;
    }
}
