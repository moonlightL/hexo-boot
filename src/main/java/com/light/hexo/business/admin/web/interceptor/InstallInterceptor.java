package com.light.hexo.business.admin.web.interceptor;

import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author MoonlightL
 * @ClassName: InstallInterceptor
 * @ProjectName hexo-boot
 * @Description: 安装拦截器
 * @DateTime 2020/9/11 17:34
 */
@Component
@Slf4j
public class InstallInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ConfigService configService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println(request.getRequestURI());

        String installValue = this.configService.getConfigValue(ConfigEnum.IS_INSTALLED.getName());
        if (!"1".equals(installValue)) {
            response.sendRedirect("/admin/install.html");
            return false;
        } else {
            if (request.getRequestURI().equals("/admin/install.html")) {
                response.sendRedirect("/admin/login.html");
            }
            return true;
        }
    }
}