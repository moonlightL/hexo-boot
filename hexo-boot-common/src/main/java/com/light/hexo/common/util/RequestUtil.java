package com.light.hexo.common.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @Author MoonlightL
 * @ClassName: RequestUtil
 * @ProjectName hexo-boot
 * @Description: Http 工具类
 * @DateTime 2020/9/9 16:07
 */
public class RequestUtil {

    /**
     * 对应 nginx 配置中的请求头，获取真实 ip 地址
     */
    private static final String[] HEADER_ARR = {"X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP"};

    private static final Integer LENGTH = 15;

    private static final String DEFAULT_SEPARATOR = ",";

    private static final String IP_V4 = "127.0.0.1";

    private static final String IP_V6 = "0:0:0:0:0:0:0:1";

    private static final String UN_KNOW = "unknown";

    private RequestUtil() {}

    /**
     * 是否是 ajax 请求
     * @param request
     * @return
     */
    public static boolean isAjax(HttpServletRequest request) {
        return !StringUtils.isEmpty(request.getHeader("x-requested-with"))
                && "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"));
    }

    /**
     * 获取客户端真实 ip
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {

        String ipAddress = request.getHeader(HEADER_ARR[0]);
        if (org.apache.commons.lang3.StringUtils.isBlank(ipAddress) || UN_KNOW.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader(HEADER_ARR[1]);
        }

        if (org.apache.commons.lang3.StringUtils.isBlank(ipAddress) || UN_KNOW.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader(HEADER_ARR[2]);
        }

        if (org.apache.commons.lang3.StringUtils.isBlank(ipAddress) || UN_KNOW.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader(HEADER_ARR[3]);
        }

        if (org.apache.commons.lang3.StringUtils.isBlank(ipAddress) || UN_KNOW.equalsIgnoreCase(ipAddress)) {

            ipAddress = request.getRemoteAddr();

            if (ipAddress.equals(IP_V4) || ipAddress.equals(IP_V6)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                ipAddress = inet.getHostAddress();
            }
        }

        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (org.apache.commons.lang3.StringUtils.isNotBlank(ipAddress)  && ipAddress.length() > LENGTH) {
            if (ipAddress.indexOf(DEFAULT_SEPARATOR) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(DEFAULT_SEPARATOR));
            }
        }

        return ipAddress;
    }

    /**
     * 获取本地主机 ip
     * @return
     */
    public static String getHostIp() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1){
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return IP_V4;
    }

    public static boolean isIpAddress(String ip) {
        String[] p = ip.split("\\.");
        if (p.length != 4) {
            return false;
        }

        for (String pp : p) {
            if (pp.length() > 3) {
                return false;
            }

            int val = Integer.valueOf(pp);
            if (val > 255) {
                return false;
            }
        }

        return true;
    }
}
