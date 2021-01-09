package com.light.hexo.common.util;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @Author MoonlightL
 * @ClassName: DateUtil
 * @ProjectName hexo-boot
 * @Description: 日期时间工具类
 * @DateTime 2020/7/29 17:18
 */
public class DateUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    /**
     *  LocalDate 转 Date
     * @param localDate
     * @return
     */
    public static Date ld2Date(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime 转 Date
     * @param localDateTime
     * @return
     */
    public static Date ldt2Date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date 转 LocalDate
     * @param date
     * @return
     */
    public static LocalDate date2LocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date 转 LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * localDate 转时间戳
     * @param localDate
     * @return
     */
    public static long toMilli(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime 转时间戳
     * @param localDateTime
     * @return
     */
    public static long toMilli(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /**
     *  LocalDate 转字符串
     * @param localDate
     * @return
     */
    public static String ldToStr(LocalDate localDate) {
        return DATE_FORMATTER.format(localDate);
    }

    /**
     * LocalDate 转字符串
     * @param localDate
     * @param formatter
     * @return
     */
    public static String ldToStr(LocalDate localDate, DateTimeFormatter formatter) {
        return formatter.format(localDate);
    }

    /**
     * LocalDateTime 转字符串
     * @param localDateTime
     * @return
     */
    public static String ldtToStr(LocalDateTime localDateTime) {
        return DATETIME_FORMATTER.format(localDateTime);
    }

    /**
     * LocalDateTime 转字符串
     * @param localDateTime
     * @param formatter
     * @return
     */
    public static String ldtToStr(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return formatter.format(localDateTime);
    }

    /**
     * Date 转字符串
     * @param date
     * @return
     */
    public static String toStr(Date date, String... patterns) {
        String pattern = (patterns.length == 0 ? DATETIME_FORMAT : patterns[0]);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    /**
     * 字符串转 LocalDate
     * @param dateStr
     * @return
     */
    public static LocalDate strToLocalDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 字符串转 LocalDateTime
     * @param dateStr
     * @return
     */
    public static LocalDateTime strToLocalDateTime(String dateStr) {
        return LocalDateTime.parse(dateStr, DATETIME_FORMATTER);
    }

    /**
     * 获取当月的第一天日期
     * @return
     */
    public static LocalDate getFirstDayOfMonth() {
        LocalDate now = LocalDate.now();
        return now.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取当前周的周一
     * @return
     */
    public static LocalDate getMonday() {
        LocalDate now = LocalDate.now();
        return now.with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.MONDAY));
    }

    /**
     * 填充日期，如果小于 0，填充 0
     * @param value
     * @return
     */
    public static String fillTime(int value) {
        return value < 10 ? "0" + value : value + "";
    }

    /**
     * 时间描述
     * @param dateTime
     * @return
     */
    public static String timeDesc(LocalDateTime dateTime) {

        LocalDateTime now = LocalDateTime.now();
        Duration between = Duration.between(dateTime, now);
        long minutes = between.toMinutes();
        if (minutes < 1) {
            return "刚刚";
        }

        if (minutes < 60) {
            return minutes + "分钟前";
        }

        long hours = between.toHours();
        if (hours < 24) {
            return hours + "小时前";
        }

        long days = between.toDays();
        if (days < 7) {
            return days + "天前";
        }

        if (days < 120) {
            return days / 7 + "周前";
        }

        if (days < 365) {
            return days / 30 + "月前";
        }

        return days / 365 + "年前";
    }
}
