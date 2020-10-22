package com.light.hexo.common.base;

import com.light.hexo.common.constant.BrowserConstant;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author MoonlightL
 * @ClassName: BaseController
 * @ProjectName hexo-boot
 * @Description: 控制器基类
 * @DateTime 2020/8/3 16:43
 */
public abstract class BaseController {

    /**
     * 列表页
     * @param map
     * @return
     */
    @RequestMapping("listUI.html")
    public String listUI(Map<String, Object> map) {
        return this.render("listUI", map);
    }

    protected String render(String pageName, Map<String, Object> resultMap) {
        String prefix = getPrefix();
        resultMap.put("baseUrl", prefix);
        return prefix + "/" + pageName;
    }

    /**
     * 获取 Prefix 地址
     * @return
     */
    protected String getPrefix() {
        RequestMapping annotation = this.getClass().getAnnotation(RequestMapping.class);
        String[] values = annotation.value();
        if (values.length == 0) {
            return "";
        }

        return annotation.value()[0];
    }

    /**
     * 下载文件
     * @param inputStream 文件输入流
     * @param fileName    文件名称
     * @param request
     * @param response
     */
    protected void download(InputStream inputStream, String fileName, HttpServletRequest request, HttpServletResponse response) {
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            this.download(data, fileName, request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     * @param data      数据数组
     * @param fileName  文件名称
     * @param request
     * @param response
     * @throws Exception
     */
    protected void download(byte[] data, String fileName, HttpServletRequest request, HttpServletResponse response) {

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + this.getFinalName(request, fileName));
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream; charset=UTF-8");

            // 写出
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.write(data, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载压缩包
     * @param filename
     * @param dataStr
     * @param request
     * @param response
     * @throws Exception
     */
    protected void downloadZip(String filename, String dataStr, HttpServletRequest request, HttpServletResponse response) {

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + this.getFinalName(request, filename) + ".zip");
            response.setContentType("application/zip; charset=UTF-8");

            // 写出
            ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
            zipOut.putNextEntry(new ZipEntry(filename));
            IOUtils.write(dataStr, zipOut, "UTF-8");
            zipOut.closeEntry();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFinalName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {

        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;

        if (agent.contains(BrowserConstant.MSIE)) {
            filename = URLEncoder.encode(filename, "UTF-8");
            filename = filename.replace("+", " ");

        } else if (agent.contains(BrowserConstant.FIREFOX)) {
            filename = new String(fileName.getBytes(), "ISO8859-1");

        } else if (agent.contains(BrowserConstant.CHROME)) {
            filename = URLEncoder.encode(filename, "UTF-8");

        } else {
            filename = URLEncoder.encode(filename, "UTF-8");
        }

        return filename;
    }
}
