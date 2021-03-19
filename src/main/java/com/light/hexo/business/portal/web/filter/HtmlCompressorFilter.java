package com.light.hexo.business.portal.web.filter;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author MoonlightL
 * @ClassName: HtmlCompressorFilter
 * @ProjectName hexo-boot
 * @Description: 页面压缩
 * @DateTime 2021/3/19 11:56
 */
@Slf4j
@WebFilter(
        filterName = "HtmlCompressorInterceptor",
        urlPatterns = "/*",
        initParams = { @WebInitParam(name="exclusions", value="/admin/*")}
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
        ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, wrapper);
        response.setContentLength(-1);
        PrintWriter out = response.getWriter();
        out.write(htmlCompressor.compress(wrapper.getResult()));
        out.flush();
    }
}
