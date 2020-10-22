package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.User;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: UserService
 * @ProjectName hexo-boot
 * @Description: 用户 Service
 * @DateTime 2020/7/30 10:31
 */
public interface UserService extends BaseService<User> {

    /**
     * 通过用户名查询用户信息
     * @param username
     * @return
     * @throws GlobalException
     */
    User findUserByUsername(String username) throws GlobalException;

    /**
     * 批量查询用户信息
     * @param uidList
     * @return
     * @throws GlobalException
     */
    List<User> listUserByIdList(List<Integer> uidList) throws GlobalException;

    /**
     * 批量删除用户
     * @param idStrList
     * @param userId
     * @throws GlobalException
     */
    void removeUserBatch(List<String> idStrList, Integer userId) throws GlobalException;

    /**
     * 修改用户信息（昵称和邮箱地址）
     * @param user
     * @throws GlobalException
     */
    void updateInfo(User user) throws GlobalException;

    /**
     * 修改用户状态
     * @param user
     * @throws GlobalException
     */
    void updateState(User user) throws GlobalException;

    /**
     *
     * @return
     * @throws GlobalException
     */
    int getUserNum() throws GlobalException;

    // ================================= 以下为前端页面请求 ===============================

    /**
     * 检测用户
     * @param nickname
     * @param email
     * @param avatar
     * @return
     */
    User checkUser(String nickname, String email, String avatar) throws GlobalException;

}
