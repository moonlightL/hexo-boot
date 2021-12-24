package com.light.hexo.business.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.AlbumDetailMapper;
import com.light.hexo.business.admin.model.AlbumDetail;
import com.light.hexo.business.admin.service.AlbumDetailService;
import com.light.hexo.business.portal.model.HexoPageInfo;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: AlbumDetailServiceImpl
 * @ProjectName hexo-boot
 * @Description: 专辑详情 Service 实现
 * @DateTime 2021/12/22, 0022 14:37
 */
@Service
public class AlbumDetailServiceImpl extends BaseServiceImpl<AlbumDetail> implements AlbumDetailService {

    @Autowired
    private AlbumDetailMapper albumDetailMapper;

    @Override
    public BaseMapper<AlbumDetail> getBaseMapper() {
        return this.albumDetailMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        return new Example(AlbumDetail.class);
    }


    @Override
    public List<AlbumDetail> findListByAlbumId(Integer albumId, Integer pageNum, Integer pageSize) throws GlobalException {
        Example example = new Example(AlbumDetail.class);
        example.createCriteria().andEqualTo("albumId", albumId);
        example.orderBy("sort ").asc();
        PageHelper.startPage(pageNum, pageSize);
        return this.albumDetailMapper.selectByExample(example);
    }

    @Override
    public void saveAlbumDetail(Integer albumId, String originalName, String url) throws GlobalException {
        AlbumDetail albumDetail = new AlbumDetail();
        albumDetail.setAlbumId(albumId)
                   .setName(originalName)
                   .setUrl(url)
                   .setSort(0)
                   .setCreateTime(LocalDateTime.now())
                   .setUpdateTime(albumDetail.getCreateTime());
        super.saveModel(albumDetail);
    }

    @Override
    public void saveAlbumDetail(AlbumDetail albumDetail) throws GlobalException {
        super.saveModel(albumDetail);
    }

    @Override
    public void updateAlbumDetail(AlbumDetail albumDetail) throws GlobalException {
        AlbumDetail db = super.findById(albumDetail.getId());
        if (db == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ALBUM_DETAIL_NOT_EXIST);
        }

        super.updateModel(albumDetail);
    }

    @Override
    public void removeAllDetail(Integer albumId) throws GlobalException {
        Example example = new Example(AlbumDetail.class);
        example.createCriteria().andEqualTo("albumId", albumId);
        this.albumDetailMapper.deleteByExample(example);
    }

    @Override
    public void removeDetail(Integer detailId) throws GlobalException {
        AlbumDetail albumDetail = super.findById(detailId);
        if (albumDetail == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ALBUM_DETAIL_NOT_EXIST);
        }
        super.removeModel(detailId);
    }

    @Override
    public Integer getAlbumDetailNum(Integer albumId) throws GlobalException {
        Example example = new Example(AlbumDetail.class);
        example.createCriteria().andEqualTo("albumId", albumId);
        return this.albumDetailMapper.selectCountByExample(example);
    }

    @Override
    public HexoPageInfo pageAlbumDetailByIndex(Integer albumId, Integer pageNum, Integer pageSize) {
        List<AlbumDetail> albumDetailList = this.findListByAlbumId(albumId, pageNum, pageSize);
        Integer totalNum = this.getAlbumDetailNum(albumId);
        return new HexoPageInfo(pageNum, pageSize, totalNum, albumDetailList);
    }
}
