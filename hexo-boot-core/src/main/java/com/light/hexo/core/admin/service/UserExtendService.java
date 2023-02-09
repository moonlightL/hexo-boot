package com.light.hexo.core.admin.service;

import com.light.hexo.common.base.BaseService;
import com.light.hexo.mapper.model.UserExtend;
import com.light.hexo.common.exception.GlobalException;

/**
 * @Author MoonlightL
 * @ClassName: UserExtendService
 * @ProjectName hexo-boot
 * @Description: 用户信息扩展 Service
 * @DateTime 2020/9/28 14:02
 */
public interface UserExtendService extends BaseService<UserExtend> {

    /**
     * 获取用户扩展信息
     * @param uid
     * @return
     * @throws GlobalException
     */
    UserExtend getUserExtendByUid(Integer uid) throws GlobalException;

    /**
     * 保存用户扩展信息
     * @param uid
     * @param desc
     */
    void saveUserExtend(Integer uid, String desc) throws GlobalException;

    // ================================= 以下为前端页面请求 ===============================

    UserExtend getBloggerInfo() throws GlobalException;
}
