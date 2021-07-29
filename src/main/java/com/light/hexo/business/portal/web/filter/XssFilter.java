package com.light.hexo.business.portal.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author: MoonlightL
 * @ClassName: XssFilter
 * @ProjectName: hexo-boot
 * @Description: 过滤内容，预防 xss 攻击
 * @DateTime: 2020/10/4 10:16
 */
@WebFilter(
    filterName = "xssFilter",
    urlPatterns = {"/auth/*"},
    initParams = { @WebInitParam(name="exclusions", value="/admin/*")}
)
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        XssHttpServletRequestWrapper req = new XssHttpServletRequestWrapper((HttpServletRequest)request);
        chain.doFilter(req, response);
    }

    @Override
    public void destroy() {

    }
}
