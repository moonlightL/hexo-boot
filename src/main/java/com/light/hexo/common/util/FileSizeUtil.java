package com.light.hexo.common.util;

/**
 * @Author MoonlightL
 * @ClassName: FileSizeUtil
 * @ProjectName hexo-boot
 * @Description: 文件大小工具
 * @DateTime 2020/9/11 15:19
 */
public class FileSizeUtil {

    private FileSizeUtil() {}

    private static final Integer SIZE = 1024;

    public static String getPrintSize(long size) {
        long rest = 0;

        if (size < SIZE) {
            return size + "B";
        } else {
            size /= SIZE;
        }

        if (size < SIZE) {
            return size + "KB";
        } else {
            rest = size % SIZE;
            size /= SIZE;
        }

        if (size < SIZE) {
            size = size * 100;
            return (size / 100) + "." + (rest * 100 / SIZE % 100) + "MB";
        } else {
            size = size * 100 / SIZE;
            return (size / 100) + "." + (size % 100) + "GB";
        }
    }
}
