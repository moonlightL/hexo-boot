package com.light.hexo.plugin.statistic.interceptor;

import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.plugin.annotation.InterceptPathPattern;
import com.light.hexo.common.util.BrowserUtil;
import com.light.hexo.common.util.IpUtil;
import com.light.hexo.plugin.statistic.event.StatisticEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import sun.net.www.content.image.png;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * @Author MoonlightL
 * @ClassName: StatisticInterceptor
 * @ProjectName hexo-boot
 * @Description: 拦截器
 * @DateTime 2022/4/25, 0025 23:10
 */
@InterceptPathPattern({"/**"})
@Slf4j
public class StatisticInterceptor implements HandlerInterceptor {

    private static final String[] FILTER_LIST = {"/admin", "/plugin", ".json", ".js", ".js.map", ".css", ".css.map", ".jpg", ".png", ".ico"};

    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        if (requestURI.contains("plugin/statistic") && requestURI.endsWith(".json")) {
            Object object = request.getSession().getAttribute(HexoConstant.CURRENT_USER);
            if (object == null) {
                log.info("==================非法请求 statistic-plugin=====================");
                response.sendRedirect("/");
                return false;
            }
        }

        for (String url : FILTER_LIST) {
            if (requestURI.contains(url)) {
                return true;
            }
        }

        String ipAddr = IpUtil.getIpAddr(request);
        String browserName = BrowserUtil.getBrowserName(request);
        this.eventPublisher.emit(new StatisticEvent(this, ipAddr, URLDecoder.decode(requestURI, "UTF-8"), browserName));

        return true;
    }
}
