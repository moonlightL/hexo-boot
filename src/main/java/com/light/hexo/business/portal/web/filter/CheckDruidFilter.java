package com.light.hexo.business.portal.web.filter;

import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.HttpUtil;
import com.light.hexo.common.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author MoonlightL
 * @ClassName: CheckDruidFilter
 * @ProjectName hexo-boot
 * @Description: 检查 druid 登录情况
 * @DateTime 2022/3/11, 0011 10:06
 */
@WebFilter(
        filterName = "checkDruidFilter",
        urlPatterns = {"/druid/*"}
)
public class CheckDruidFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Object obj = httpServletRequest.getSession().getAttribute(HexoConstant.CURRENT_USER);
        if (obj == null) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.sendRedirect("/admin/login.html");
            return;
        }
        chain.doFilter(request, response);
    }
}
