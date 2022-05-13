package com.light.hexo.common.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * @Author MoonlightL
 * @ClassName: IpUtil
 * @ProjectName hexo-boot
 * @Description: ip 地址工具类
 * @DateTime 2020/9/7 17:36
 */
@Slf4j
public class IpUtil {

    private static DbSearcher searcher;

    private static Method method;

    static {
        try (InputStream inputStream = IpUtil.class.getClassLoader().getResourceAsStream("ip2region.db")) {
            // 打包后无法读取 db 文件， 因此使用临时文件方式解决
            File dbFile = new File(System.getProperties().getProperty("java.io.tmpdir"), "ip.db");
            if (dbFile.exists()) {
                dbFile.delete();
            }
            FileUtils.copyInputStreamToFile(inputStream, dbFile);
            String ipDbPath = System.getProperties().getProperty("java.io.tmpdir") + "/ip.db";
            searcher = new DbSearcher(new DbConfig(), ipDbPath);
            // btreeSearch  binarySearch  memorySearch
            method = searcher.getClass().getMethod("memorySearch", String.class);
        } catch (Exception e) {
            log.error("=============== IpUtil 加载 ip2region.db 文件失败==================");
            e.printStackTrace();
        }
    }

    private IpUtil() {}

    /**
     * 获取 ip 对应的信息
     * @param ip
     * @return  格式： 中国|0|浙江|杭州|电信
     */
    public static IpInfo getInfo(String ip) {

        IpInfo ipInfo = new IpInfo(ip);

        if (!Util.isIpAddress(ip)) {
            log.warn("========非法 IP 格式========");
            return ipInfo;
        }

        try {
            DataBlock dataBlock = (DataBlock) method.invoke(searcher, ip);
            String region = dataBlock.getRegion();
            String[] array = region.split("\\|");
            ipInfo.setCountry(array[0]).setProvince(array[2]).setCity(array[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ipInfo;
    }

    @Setter
    @Getter
    @Accessors(chain = true)
    @ToString
    public static class IpInfo {

        private String country = "未知";

        private String province = "未知";

        private String city = "未知";

        private String ip;

        public IpInfo(String ip) {
            this.ip = ip;
        }
    }

}
