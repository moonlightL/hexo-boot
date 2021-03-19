package com.light.hexo.business.portal.web.filter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @Author MoonlightL
 * @ClassName: ResponseWrapper
 * @ProjectName hexo-boot
 * @Description: 重写响应
 * @DateTime 2021/3/19 11:58
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

    private PrintWriter cachedWriter;

    private CharArrayWriter bufferedWriter;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        bufferedWriter = new CharArrayWriter();
        cachedWriter = new PrintWriter(bufferedWriter);
    }


    @Override
    public PrintWriter getWriter() throws IOException {
        return cachedWriter;
    }

    /**
     * 获取原始的HTML页面内容。
     */
    public String getResult() {
        try {
            byte[] bytes = bufferedWriter.toString().getBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } finally {
            cachedWriter.close();
            bufferedWriter.close();
        }
    }
}
