package com.light.hexo.common.util;

import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author MoonlightL
 * @ClassName: CacheUtil
 * @ProjectName hexo-boot
 * @Description: 缓存工具类（使用本地内存，如果使用第三方缓存，如 redis，可以忽略该类）
 * @DateTime 2020/9/9 15:42
 */
public class CacheUtil {

    private static final Map<String, Cache> CACHE_MAP = new ConcurrentHashMap<>();

    private CacheUtil() {}

    /**
     * 保存数据
     * @param key
     * @param value
     * @param <T>
     */
    public static <T> void put(String key, T value) {
        CACHE_MAP.put(key, new Cache<T>(value));
    }

    /**
     * 保存数据
     * @param key
     * @param value
     * @param duration  时长，单位：毫秒
     * @param <T>
     */
    public static <T> void put(String key, T value, long duration) {
        CACHE_MAP.put(key, new Cache<T>(value, duration));
    }

    /**
     * 获取数据
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T get(String key) {
        Cache<T> cache = CACHE_MAP.get(key);
        if (cache == null) {
            return null;
        }

        if (!cache.hasTime) {
            return cache.getData();
        }

        if (System.currentTimeMillis() < cache.getTimeout()) {
            return cache.getData();
        }

        // 过期清除
        CACHE_MAP.remove(key);
        return null;
    }

    /**
     * 删除
     * @param key
     */
    public static void remove(String key) {
        CACHE_MAP.remove(key);
    }

    /**
     *  key 集合
     * @return
     */
    public static Set<String> keySet() {
        return CACHE_MAP.keySet();
    }

    /**
     * 是否过期
     * @param key
     * @return
     */
    public static <T> boolean isExpire(String key) {
        Cache<T> cache = CACHE_MAP.get(key);
        if (cache == null) {
            return false;
        }

        if (!cache.hasTime) {
            return false;
        }

        return System.currentTimeMillis() >= cache.getTimeout();
    }

    public static synchronized Integer incr(String key) {
        Integer num = get(key);
        if (num == null) {
            num = 1;
            put(key, num);
        } else {
            num++;
            put(key, num);
        }

        return num;
    }

    @Getter
    static class Cache<T> {

        /**
         * 过期时间，单位：毫秒
         */
        private long timeout;

        /**
         * 是否有时间
         */
        private boolean hasTime;

        /**
         * 数据
         */
        private T data;

        private Cache(T data) {
            this.data = data;
        }

        private Cache(T data, long duration) {
            this.data = data;
            this.timeout = System.currentTimeMillis() + duration;
            this.hasTime = true;
        }
    }
}
