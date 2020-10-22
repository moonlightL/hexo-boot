package com.light.hexo.common.util;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;

/**
 * @Author MoonlightL
 * @ClassName: EhcacheUtil
 * @ProjectName hexo-boot
 * @Description: ehcache 缓存工具类
 * @DateTime 2020/9/28 10:40
 */
public class EhcacheUtil {

    private EhcacheUtil() {}

    /**
     * 通过名称清除缓存
     * @param cacheName
     */
    public static void clearByCacheName(String cacheName) {
        CacheManager cacheManager = SpringContextUtil.getBeanByType(CacheManager.class);
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * 清除所有缓存
     */
    public static void clearAll() {
        CacheManager cacheManager = SpringContextUtil.getBeanByType(CacheManager.class);
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
    }
}
