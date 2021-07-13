package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.Blacklist;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: BlacklistService
 * @ProjectName hexo-boot
 * @Description: 黑名单 Service
 * @DateTime 2020/9/9 15:35
 */
public interface BlacklistService extends BaseService<Blacklist> {

    /**
     * 是否在黑名单中
     * @param ip
     * @return
     * @throws GlobalException
     */
    boolean isBlacklist(String ip) throws GlobalException;

    /**
     * 添加黑名单
     * @param ipAddr
     * @param remark
     * @param hour    null 表示永久
     * @throws GlobalException
     */
    void saveBlacklist(String ipAddr, String remark, Integer hour) throws GlobalException;

    /**
     * 获取临时黑名单
     * @param dateTime
     * @return
     * @throws GlobalException
     */
    List<Blacklist> listExpireBlacks(LocalDateTime dateTime) throws GlobalException;
}
