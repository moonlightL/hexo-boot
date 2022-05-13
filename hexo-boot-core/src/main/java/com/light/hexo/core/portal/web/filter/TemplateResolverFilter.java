package com.light.hexo.core.portal.web.filter;

import com.light.hexo.common.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.NestedServletException;
import org.thymeleaf.exceptions.TemplateInputException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author MoonlightL
 * @ClassName: TemplateResolverFilter
 * @ProjectName hexo-boot
 * @Description: 处理视图解析器报错问题
 * @DateTime 2022/5/10, 0010 17:08
 */
@Slf4j
@WebFilter(
        filterName = "TemplateResolverFilter",
        urlPatterns = "/*"
)
public class TemplateResolverFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            log.error("TemplateResolverFilter Error : {}", e);
            boolean ajax = RequestUtil.isAjax((HttpServletRequest) servletRequest);
            if (!ajax) {
                String path;
                if (e instanceof NestedServletException || e instanceof TemplateInputException) {
                    path = "/error/404.html";
                } else {
                    path = "/error/5xx.html";
                }
                servletRequest.getRequestDispatcher(path).forward(servletRequest, servletResponse);
            }
        }
    }
}
