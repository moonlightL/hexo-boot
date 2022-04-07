package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.AlbumDetail;
import com.light.hexo.business.portal.model.HexoPageInfo;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: AlbumDetailService
 * @ProjectName hexo-boot
 * @Description: 专辑详情 Service
 * @DateTime 2021/12/22, 0022 14:37
 */
public interface AlbumDetailService extends BaseService<AlbumDetail> {

    /**
     * 根据专辑 id 获取详情列表
     * @param albumId
     * @param pageNum
     * @param pageSize
     * @return
     * @throws GlobalException
     */
    List<AlbumDetail> findListByAlbumId(Integer albumId, Integer pageNum, Integer pageSize) throws GlobalException;

    /**
     * 保存专辑详情
     * @param albumId
     * @param originalName
     * @param url
     * @param coverUrl
     * @throws GlobalException
     */
    void saveAlbumDetail(Integer albumId, String originalName, String url, String coverUrl) throws GlobalException;

    /**
     * 保存专辑详情
     * @param albumDetail
     * @throws GlobalException
     */
    void saveAlbumDetail(AlbumDetail albumDetail);

    /**
     * 修改专辑详情
     * @param albumDetail
     * @throws GlobalException
     */
    void updateAlbumDetail(AlbumDetail albumDetail) throws GlobalException;

    /**
     * 删除全部的专辑详情
     * @param albumId
     */
    void removeAllDetail(Integer albumId) throws GlobalException;

    /**
     * 删除指定专辑详情
     * @param detailId
     * @throws GlobalException
     */
    void removeDetail(Integer detailId) throws GlobalException;

    /**
     * 获取专辑详情数
     * @param albumId
     * @return
     * @throws GlobalException
     */
    Integer getAlbumDetailNum(Integer albumId) throws GlobalException;

    // ================================= 以下为前端页面请求 ===============================

    HexoPageInfo pageAlbumDetailByIndex(Integer albumId, Integer pageNum, Integer pageSize);

}
