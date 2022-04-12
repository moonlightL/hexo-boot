package com.light.hexo.core.admin.service;

import com.light.hexo.common.base.BaseService;
import com.light.hexo.mapper.model.Music;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: MusicService
 * @ProjectName hexo-boot
 * @Description: 音乐 Service
 * @DateTime 2021/2/3 15:46
 */
public interface MusicService extends BaseService<Music> {

    /**
     * 批量删除音乐
     * @param idStrList
     */
    void removeMusicBatch(List<String> idStrList);

    /**
     * 获取音乐列表（首页）
     * @return
     */
    List<Music> listMusicByIndex();
}
