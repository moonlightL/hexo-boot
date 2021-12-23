package com.light.hexo.business.admin.model.extend;

import com.light.hexo.common.util.FileSizeUtil;
import com.light.hexo.common.util.IpUtil;
import lombok.*;
import lombok.experimental.Accessors;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.VirtualMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @Author MoonlightL
 * @ClassName: ServerInfo
 * @ProjectName hexo-boot
 * @Description: 服务器参数信息
 * @DateTime 2020/9/24 11:23
 */
@Setter
@Getter
@Accessors(chain = true)
@ToString
public class ServerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Integer SIZE = 1024;

    /**
     * 基本信息
     */
    private Basic basic;

    /**
     * cpu 信息
     */
    private Cpu cpu;

    /**
     * 硬盘信息
     */
    private List<Hardware> hardwareList;

    /**
     * 内存信息
     */
    private Memory memory;

    /**
     * jvm 信息
     */
    private Jvm jvm;

    public void collectInfo() {

        Properties props = System.getProperties();

        // 搜集基本信息
        this.collectBasicInfo(props);

        // 搜集 JVM 信息
        this.collectJvmInfo(props);

        SystemInfo sysInfo = new SystemInfo();

        // 搜集 cpu 信息
        this.collectCpuInfo(sysInfo);

        // 搜集 内存信息
        this.collectMemoryInfo(sysInfo);

        // 搜集硬盘信息
        this.collectHardwareInfo(sysInfo);

    }

    private void collectBasicInfo(Properties props) {
        Basic basicInfo = new Basic();
        basicInfo.setSysName(props.getProperty("os.name"));
        basicInfo.setSysArch(props.getProperty("os.arch"));
        basicInfo.setSysVersion(props.getProperty("os.version"));
        basicInfo.setSysIp(IpUtil.getHostIp());
        basicInfo.setUserDir(props.getProperty("user.dir"));

        this.setBasic(basicInfo);
    }

    private void collectJvmInfo(Properties props) {
        Jvm jvmInfo = new Jvm();
        jvmInfo.setHome(props.getProperty("java.home"));
        jvmInfo.setVersion(props.getProperty("java.version"));

        Runtime runtime = Runtime.getRuntime();
        // jvm 总内存
        long totalMemory = runtime.totalMemory();
        jvmInfo.setTotalMemory(FileSizeUtil.getPrintSize(totalMemory));
        // jvm 空闲内容
        long freeMemory = runtime.freeMemory();
        jvmInfo.setFreeMemory(FileSizeUtil.getPrintSize(freeMemory));
        // jvm 已使用内容
        jvmInfo.setRunTimeMemory(FileSizeUtil.getPrintSize(totalMemory - freeMemory));

        this.setJvm(jvmInfo);
    }

    private void collectCpuInfo(SystemInfo sysInfo) {

        Cpu cpuInfo = new Cpu();
        CentralProcessor processor = sysInfo.getHardware().getProcessor();
        // 名称
        cpuInfo.setName(processor.getProcessorIdentifier().getName());
        // 线程数
        cpuInfo.setThreadCount(sysInfo.getOperatingSystem().getThreadCount());
        // 内核数
        cpuInfo.setPhysicalCount(processor.getPhysicalProcessorCount());
        // 逻辑处理数
        cpuInfo.setLogicalCount(processor.getLogicalProcessorCount());

        this.setCpu(cpuInfo);
    }

    private void collectMemoryInfo(SystemInfo sysInfo) {
        GlobalMemory globalMemory = sysInfo.getHardware().getMemory();
        Memory memoryInfo = new Memory();
        // 系统空闲内存
        long available = globalMemory.getAvailable();
        memoryInfo.setAvailable(FileSizeUtil.getPrintSize(available));
        // 系统总内存
        long total = globalMemory.getTotal();
        memoryInfo.setTotal(FileSizeUtil.getPrintSize(total));
        // 系统已使用内存
        memoryInfo.setUsed(FileSizeUtil.getPrintSize(total - available));
        // 交换内存相关
        VirtualMemory virtualMemory = globalMemory.getVirtualMemory();
        memoryInfo.setSwapUsed(FileSizeUtil.getPrintSize(virtualMemory.getSwapUsed()));
        memoryInfo.setSwapTotal(FileSizeUtil.getPrintSize(virtualMemory.getSwapTotal()));

        this.setMemory(memoryInfo);
    }

    private void collectHardwareInfo(SystemInfo sysInfo) {
        FileSystem fileSystem = sysInfo.getOperatingSystem().getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();

        List<Hardware> hardwareList = new ArrayList<Hardware>();
        Hardware hardwareInfo;
        for (OSFileStore osFileStore : fileStores) {
            hardwareInfo = new Hardware();
            // 盘符名
            hardwareInfo.setName(osFileStore.getName());
            // 盘符格式
            hardwareInfo.setType(osFileStore.getType());
            // 可用容量
            long freeSpace = osFileStore.getUsableSpace();
            hardwareInfo.setFreeSpace(FileSizeUtil.getPrintSize(freeSpace));
            // 总容量
            long totalSpace = osFileStore.getTotalSpace();
            hardwareInfo.setTotalSpace(FileSizeUtil.getPrintSize(totalSpace));
            // 已使用容量
            hardwareInfo.setUsabledSpace(FileSizeUtil.getPrintSize(totalSpace - freeSpace));
            hardwareList.add(hardwareInfo);
        }

        this.setHardwareList(hardwareList);
    }



    @Setter
    @Getter
    @NoArgsConstructor
    static class Basic {

        /**
         * 服务器名称
         */
        private String sysName;

        /**
         * 服务器架构 32/64
         */
        private String sysArch;

        /**
         * 服务器版本
         */
        private String sysVersion;

        /**
         * 服务器 ip
         */
        private String sysIp;

        /**
         * 项目部署目录
         */
        private String userDir;
    }


    @Setter
    @Getter
    @NoArgsConstructor
    static class Cpu {

        /**
         * 线程数
         */
        private Integer threadCount;

        /**
         * 名称
         */
        private String name;

        /**
         * 内核数
         */
        private Integer physicalCount;

        /**
         * 逻辑处理数
         */
        private Integer logicalCount;

    }

    @Setter
    @Getter
    @NoArgsConstructor
    static class Hardware {

        /**
         * 盘符名称
         */
        private String name;

        /**
         * 格式：
         * windows下主要有FAT16、FAT32、NTFS 等
         * linux下的格式为ext系列，ext4，ext3等
         * Mac OS X的硬盘格式是APFS
         */
        private String type;

        /**
         * 空闲容量
         */
        private String freeSpace;

        /**
         * 已使用容量
         */
        private String usabledSpace;

        /**
         * 总容量
         */
        private String totalSpace;

    }

    @Setter
    @Getter
    @NoArgsConstructor
    static class Jvm {

        /**
         * jdk 版本
         */
        private String version;

        /**
         * 安装目录
         */
        private String home;

        /**
         * 总内存
         */
        private String totalMemory;

        /**
         * 可用内存
         */
        private String freeMemory;

        /**
         * 运行时内存
         */
        private String runTimeMemory;

        /**
         * JDK启动时间
         */
        public String getStartTime() {
            long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
            Date date = new Date(startTime);
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return formatter.format(localDateTime);
        }

        /**
         * JDK运行时间
         */
        public String getRunTime() {

            LocalDateTime now = LocalDateTime.now();

            long time = ManagementFactory.getRuntimeMXBean().getStartTime();
            Date date = new Date(time);
            LocalDateTime startTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            Duration between = Duration.between(startTime, now);

            long day = between.toDays();

            long hour = between.toHours() - (day * 24);

            long minute = between.toMinutes() - (day * 24 * 60) - (hour * 60);

            return String.format("运行%d天%d小时%d分钟", day, hour, minute);
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    static class Memory {

        /**
         * 可用内存
         */
        private String available;

        /**
         * 已使用内存
         */
        private String used;

        /**
         * 总内存
         */
        private String total;

        /**
         * 已使用交换内存
         */
        private String swapUsed;

        /**
         * 总交换内存
         */
        private String swapTotal;
    }
}
