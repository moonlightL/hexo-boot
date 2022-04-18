package com.light.hexo.core.admin.service.impl;

import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.core.admin.constant.HexoExceptionEnum;
import com.light.hexo.mapper.mapper.AlbumMapper;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.model.Album;
import com.light.hexo.common.event.AlbumEvent;
import com.light.hexo.core.admin.service.AlbumDetailService;
import com.light.hexo.core.admin.service.AlbumService;
import com.light.hexo.core.portal.constant.PageConstant;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.request.AlbumRequest;
import com.light.hexo.common.util.EhcacheUtil;
import com.light.hexo.common.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: AlbumServiceImpl
 * @ProjectName hexo-boot
 * @Description: 附件 Service 实现
 * @DateTime 2021/12/21, 0021 15:45
 */
@CacheConfig(cacheNames = "albumCache")
@Service
@Slf4j
public class AlbumServiceImpl extends BaseServiceImpl<Album> implements AlbumService {

    @Autowired
    private AlbumMapper albumMapper;

    @Autowired
    private AlbumDetailService albumDetailService;

    @Autowired
    @Lazy
    private EventPublisher eventPublisher;

    @Override
    public BaseMapper<Album> getBaseMapper() {
        return this.albumMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {

        AlbumRequest albumRequest = (AlbumRequest) request;
        Example example = new Example(Album.class);
        Example.Criteria criteria = example.createCriteria();

        String name = albumRequest.getName();
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", name + "%");
        }

        example.orderBy("id").desc();
        return example;
    }

    @Override
    public void saveAlbum(Album album) throws GlobalException {

        Album db = this.getAlbumByName(album.getName().trim());
        if (db != null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ALBUM_NAME_REPEAT);
        }

        if (StringUtils.isBlank(album.getCoverUrl())) {
            album.setCoverUrl(HexoConstant.DEFAULT_CATEGORY_COVER);
        }

        if (album.getVisitType().equals(1)) {
            album.setVisitCode("");
        }

        this.saveModel(album);

        // 清除缓存
        this.eventPublisher.emit(new AlbumEvent());
    }

    @Override
    public void updateAlbum(Album album) throws GlobalException {
        if (StringUtils.isBlank(album.getCoverUrl())) {
            album.setCoverUrl(HexoConstant.DEFAULT_CATEGORY_COVER);
        }

        if (album.getVisitType().equals(1)) {
            album.setVisitCode("");
        }

        this.updateModel(album);

        // 清除缓存
        this.eventPublisher.emit(new AlbumEvent());
    }

    @Override
    public void removeAlbum(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());
        Integer albumId = idList.get(0);
        Album db = this.findById(albumId);
        if (db == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ALBUM_NOT_EXIST);
        }

        Integer detailNum = this.albumDetailService.getAlbumDetailNum(albumId);
        if (detailNum > 0) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ALBUM_DETAIL_NOT_EMPTY);
        }

        this.removeModel(albumId);

        // 清除缓存
        this.eventPublisher.emit(new AlbumEvent());
    }

    @Cacheable(key = "'" + PageConstant.ALBUM_LIST + "'")
    @Override
    public List<Album> listAlbumsByIndex() throws GlobalException {
        List<Album> albumList = super.findAll();
        if (!CollectionUtils.isEmpty(albumList)) {
            for (Album album : albumList) {
                album.setDetailNum(this.albumDetailService.getAlbumDetailNum(album.getId()));
            }
        }
        return albumList;
    }

    private Album getAlbumByName(String name) throws GlobalException {
        Example example = new Example(Album.class);
        example.createCriteria().andEqualTo("name", name);
        return this.getBaseMapper().selectOneByExample(example);
    }

    @Override
    public EventEnum getEventType() {
        return EventEnum.ALBUM;
    }

    @Override
    public void dealWithEvent(BaseEvent event) {
        EhcacheUtil.clearByCacheName("albumCache");
    }
}
