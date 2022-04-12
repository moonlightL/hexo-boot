package com.light.hexo.common.util;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author MoonlightL
 * @ClassName: DownloadUtil
 * @ProjectName hexo-boot
 * @Description: 下载工具
 * @DateTime 2020/8/3 16:52
 */
public class DownloadUtil {

    /**
     * IE浏览器
     */
    public static final String MSIE = "MSIE";

    /**
     * 火狐浏览器
     */
    public static final String FIREFOX = "Firefox";

    /**
     * google 浏览器
     */
    public static final String CHROME = "Chrome";

    /**
     * 下载文件
     * @param fileUrl  文件访问路径
     * @param fileName 文件名称
     * @param request
     * @param response
     * @throws Exception
     */
    protected void download(String fileUrl, String fileName, HttpServletRequest request, HttpServletResponse response) {

        try {
            byte[] data = IOUtils.toByteArray(new URL(fileUrl));
            this.download(data, fileName, request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * 打包压缩包
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

        if (agent.contains(DownloadUtil.MSIE)) {
            filename = URLEncoder.encode(filename, "UTF-8");
            filename = filename.replace("+", " ");

        } else if (agent.contains(DownloadUtil.FIREFOX)) {
            filename = new String(fileName.getBytes(), "ISO8859-1");

        } else if (agent.contains(DownloadUtil.CHROME)) {
            filename = URLEncoder.encode(filename, "UTF-8");

        } else {
            filename = URLEncoder.encode(filename, "UTF-8");
        }

        return filename;
    }
}
