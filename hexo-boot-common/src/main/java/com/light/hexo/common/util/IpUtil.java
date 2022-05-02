package com.light.hexo.common.util;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import java.io.File;
import java.io.IOException;
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

    static {
        try (InputStream inputStream = IpUtil.class.getClassLoader().getResourceAsStream("ip2region.db")) {
            // 打包后无法读取 db 文件， 因此使用临时文件方式解决
            FileUtils.copyInputStreamToFile(inputStream, new File(System.getProperties().getProperty("java.io.tmpdir") , "ip.db"));
        } catch (IOException e) {
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

        int algorithm = DbSearcher.MEMORY_ALGORITYM;
        DbSearcher searcher = null;
        try {
            String ipDbPath = System.getProperties().getProperty("java.io.tmpdir") + "/ip.db";
            searcher = new DbSearcher(new DbConfig(), ipDbPath);
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
            }

            DataBlock dataBlock = (DataBlock) method.invoke(searcher, ip);
            String region = dataBlock.getRegion();
            String[] array = region.split("\\|");
            ipInfo.setCountry(array[0]).setProvince(array[2]).setCity(array[3]);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("======== ip 查询异常========");
        }

        return ipInfo;
    }

    @Setter
    @Getter
    @Accessors(chain = true)
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
