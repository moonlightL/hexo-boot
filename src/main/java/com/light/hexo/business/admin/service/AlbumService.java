package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.Album;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.exception.GlobalException;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: AlbumService
 * @ProjectName hexo-boot
 * @Description: 专辑 Service
 * @DateTime 2021/12/21, 0021 15:45
 */
public interface AlbumService extends BaseService<Album>, EventService {

    /**
     * 保存专辑
     * @param album
     * @throws GlobalException
     */
    void saveAlbum(Album album) throws GlobalException;

    /**
     * 修改专辑
     * @param album
     * @throws GlobalException
     */
    void updateAlbum(Album album) throws GlobalException;

    /**
     * 删除专辑
     * @param idStrList
     */
    void removeAlbum(List<String> idStrList) throws GlobalException;

    // =========================== 以下为前端页面请求 ============================

    /**
     * 专辑列表（首页）
     * @return
     * @throws GlobalException
     */
    List<Album> listAlbumsByIndex() throws GlobalException;
}
