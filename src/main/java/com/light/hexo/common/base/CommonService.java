package com.light.hexo.common.base;

import com.github.pagehelper.PageInfo;
import com.light.hexo.common.exception.GlobalException;

import java.io.Serializable;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: CommonService
 * @ProjectName hexo-boot
 * @Description: 通用的 Service
 * @DateTime 2022/1/27, 0027 13:47
 */
public interface CommonService<T, M extends BaseMapper> {


    /** 添加对象
     * @param model
     * @return
     * @throws GlobalException
     */
    int saveModel(T model) throws GlobalException;

    /**
     * 删除对象
     * @param id
     * @return
     * @throws GlobalException
     */
    int removeModel(Serializable id) throws GlobalException;

    /**
     * 批量删除
     * @param idList
     * @return
     * @throws GlobalException
     */
    int removeBatch(List<? extends Serializable> idList) throws GlobalException;

    /**
     * 修改对象
     * @param model
     * @return
     * @throws GlobalException
     */
    int updateModel(T model) throws GlobalException;

    /**
     * 根据 id 获取对象
     * @param id
     * @return
     * @throws GlobalException
     */
    T findById(Serializable id) throws GlobalException;

    /**
     * 查询所有列表（列表形式）
     * @return
     * @throws GlobalException
     */
    List<T> findAll() throws GlobalException;

    /**
     * 条件查询列表
     * @param request
     * @return
     * @throws GlobalException
     */
    List<T> findAll(BaseRequest<T> request) throws GlobalException;

    /**
     * 分页查询
     * @param request
     * @return
     * @throws GlobalException
     */
    PageInfo<T> findPage(BaseRequest<T> request) throws GlobalException;
}
