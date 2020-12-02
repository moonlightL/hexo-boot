package com.light.hexo.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @Author MoonlightL
 * @ClassName: IpUtil
 * @ProjectName hexo-boot
 * @Description: ip 地址工具类
 * @DateTime 2020/9/7 17:36
 */
@Slf4j
public class IpUtil {

    private static final String IP_V4 = "127.0.0.1";

    private static final String IP_V6 = "0:0:0:0:0:0:0:1";

    private static final String UNKNOW = "unknown";

    private static final Integer LENGTH = 15;

    private static final String DEFAULT_SEPARATOR = ",";

    private static DbSearcher searcher;

    private IpUtil() {}

    private static DbSearcher getSearcher() throws Exception {
        InputStream inputStream = IpUtil.class.getClassLoader().getResourceAsStream("ip2region.db");
        // 打包后无法读取 db 文件， 因此使用临时文件方式解决
        File file = new File(System.getProperties().getProperty("java.io.tmpdir") , "ip.db");
        FileUtils.copyInputStreamToFile(inputStream, file);

        if (!file.exists()) {
            log.error("=======ip2region.db 文件不存在=========");
            return null;
        }

        //查询算法
        DbConfig config = new DbConfig();
        return new DbSearcher(config, file.getAbsolutePath());
    }

    public static String getInfo(String ip) {

        if (!Util.isIpAddress(ip)) {
            log.warn("========非法 IP 格式========");
            return "神秘";
        }

        DbSearcher searcher = null;

        try {
            searcher = getSearcher();
            int algorithm = DbSearcher.BTREE_ALGORITHM;
            Method method = null;
            switch (algorithm) {
                case DbSearcher.BTREE_ALGORITHM:
                    method = searcher.getClass().getMethod("btreeSearch", String.class);
                    break;
                case DbSearcher.BINARY_ALGORITHM:
                    method = searcher.getClass().getMethod("binarySearch", String.class);
                    break;
                case DbSearcher.MEMORY_ALGORITYM:
                    method = searcher.getClass().getMethod("memorySearch", String.class);
                    break;
                default:
                    break;
            }

            DataBlock dataBlock;
            dataBlock  = (DataBlock) method.invoke(searcher, ip);

            return dataBlock.getRegion();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("======== ip 查询异常========");
        }

        return "神秘";
    }

    public static String getProvinceAndCity(String ip) {
        String info = getInfo(ip);
        String[] split = info.split("\\|");
        String result = !StringUtils.isBlank(info) ? split[2] + split[3] : "神秘";
        if (result.contains("内网")) {
            return "神秘";
        }
        return result;
    }

    /**
     * 获取省份
     * @param ip
     * @return
     */
    public static String getProvince(String ip) {
        String info = getInfo(ip);
        String result = !StringUtils.isBlank(info) ? info.split("\\|")[2] : "神秘";
        if (result.contains("内网")) {
            return "神秘";
        }
        return result;
    }

    /**
     * 获取省份
     * @param request
     * @return
     */
    public static String getProvince(HttpServletRequest request) {
        return getProvince(getIpAddr(request));
    }

    /**
     * 获取城市
     * @param ip
     * @return
     */
    public static String getCity(String ip) {
        String info = getInfo(ip);
        String result = !StringUtils.isBlank(info) ? info.split("\\|")[3] : "神秘";
        if (result.contains("内网")) {
            return "神秘";
        }
        return result;
    }

    /**
     * 获取城市
     * @param request
     * @return
     */
    public static String getCity(HttpServletRequest request) {
        return getCity(getIpAddr(request));
    }

    /**
     * 获取客户端真实IP
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {

        String ipAddress = request.getHeader("x-forwarded-for");

        if (ipAddress == null || ipAddress.length() == 0 || UNKNOW.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.length() == 0 || UNKNOW.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.length() == 0 || UNKNOW.equalsIgnoreCase(ipAddress)) {

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
        if (ipAddress != null && ipAddress.length() > LENGTH) {
            if (ipAddress.indexOf(DEFAULT_SEPARATOR) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(DEFAULT_SEPARATOR));
            }
        }
        return ipAddress;
    }

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

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return UNKNOW;
    }
}
