package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.Category;
import com.light.hexo.business.admin.model.Nav;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: NavService
 * @ProjectName hexo-boot
 * @Description: 导航 Service
 * @DateTime 2020/12/14 17:32
 */
public interface NavService extends BaseService<Nav> {

    /**
     * 获取导航信息
     * @param link
     * @return
     */
    Nav findByLink(String link) throws GlobalException;

    /**
     * 获取导航列表
     * @return
     * @throws GlobalException
     */
    List<Nav> listNavsByIndex() throws GlobalException;

    /**
     * 批量删除
     * @param idStrList
     * @throws GlobalException
     */
    void removeNavBatch(List<String> idStrList) throws GlobalException;

    /**
     * 保存导航
     * @param nav
     * @throws GlobalException
     */
    void saveNav(Nav nav) throws GlobalException;

    /**
     * 编辑导航
     * @param nav
     * @throws GlobalException
     */
    void updateNav(Nav nav) throws GlobalException;
}
