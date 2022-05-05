package com.light.hexo.core.portal.web.interceptor;

import com.light.hexo.common.vo.Result;
import com.light.hexo.common.event.VisitEvent;
import com.light.hexo.core.admin.service.BlacklistService;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @Author MoonlightL
 * @ClassName: VisitInterceptor
 * @ProjectName hexo-boot
 * @Description: 访问拦截器
 * @DateTime 2020/9/10 9:38
 */
@Component
@Slf4j
public class VisitInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ipAddr = RequestUtil.getIpAddr(request);
        if (this.blacklistService.isBlacklist(ipAddr)) {
            if (RequestUtil.isAjax(request)) {
                this.print(response, JsonUtil.obj2String(Result.fail(GlobalExceptionEnum.ERROR_IN_BLACKLIST)));
            } else {
                ExceptionUtil.throwExToPage(GlobalExceptionEnum.ERROR_IN_BLACKLIST);
            }
            log.info("==============VisitInterceptor 黑名单用户访问: {}=====================", ipAddr);
            return false;
        }

        this.eventPublisher.emit(new VisitEvent(this, ipAddr, BrowserUtil.getBrowserName(request)));

        request.setAttribute("time", System.currentTimeMillis());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        long start = (long) request.getAttribute("time");
        log.info("=========VisitInterceptor afterCompletion 请求 {}, 耗时 {} ms========", URLDecoder.decode(request.getRequestURI(), "UTF-8"), (System.currentTimeMillis() - start));
    }

    private void print(HttpServletResponse response, String result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(result.getBytes(StandardCharsets.UTF_8));
    }
}
