package com.light.hexo.core.admin.service;

import com.light.hexo.common.base.BaseService;
import com.light.hexo.mapper.model.Nav;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.exception.GlobalException;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: NavService
 * @ProjectName hexo-boot
 * @Description: 导航 Service
 * @DateTime 2020/12/14 17:32
 */
public interface NavService extends BaseService<Nav>, EventService {

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
    List<Nav> listNavs() throws GlobalException;

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

    /**
     * 获取父级导航，即 parent_id = 0 的数据
     * @return
     * @throws GlobalException
     */
    List<Nav> listParentNav() throws GlobalException;

    /**
     * 加载数据
     * @param servletContext
     */
    void loadNav(ServletContext servletContext);

    /**
     * 获取自定义导航
     * @param link
     * @return
     * @throws GlobalException
     */
    Nav findCustomLink(String link) throws GlobalException;
}
