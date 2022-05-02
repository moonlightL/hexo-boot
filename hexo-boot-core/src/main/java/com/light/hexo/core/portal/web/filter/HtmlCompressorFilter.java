package com.light.hexo.core.portal.web.filter;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.light.hexo.common.constant.RequestFilterConstant;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author MoonlightL
 * @ClassName: HtmlCompressorFilter
 * @ProjectName hexo-boot
 * @Description: 页面压缩
 * @DateTime 2021/3/19 11:56
 */
@Slf4j
@WebFilter(
        filterName = "HtmlCompressorFilter",
        urlPatterns = "/*"
)
public class HtmlCompressorFilter implements Filter {

    private HtmlCompressor htmlCompressor;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        htmlCompressor = new HtmlCompressor();
        htmlCompressor.setCompressCss(true);
        htmlCompressor.setCompressJavaScript(true);
        htmlCompressor.setRemoveComments(true);
        htmlCompressor.setRemoveIntertagSpaces(true);
        htmlCompressor.setRemoveMultiSpaces(true);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();

        for (String url : RequestFilterConstant.FILTER_START_URL) {
            if (requestURI.startsWith(url)) {
                chain.doFilter(request, response);
                return;
            }
        }

        for (String url : RequestFilterConstant.FILTER_END_URL) {
            if (requestURI.endsWith(url)) {
                chain.doFilter(request, response);
                return;
            }
        }

        HtmlResponseWrapper responseWrapper = new HtmlResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, responseWrapper);
        response.setContentLength(-1);

        if (!response.isCommitted()) {
            String content = responseWrapper.toString();
            try {
                response.getWriter().write(this.htmlCompressor.compress(content));
            } catch (Exception e) {
                response.getWriter().write(content);
            }
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
