package com.light.hexo.core.admin.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.github.pagehelper.PageHelper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.config.BlogConfig;
import com.light.hexo.common.constant.ConfigEnum;
import com.light.hexo.common.constant.HexoExceptionEnum;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.RequestUtil;
import com.light.hexo.core.admin.service.AlbumDetailService;
import com.light.hexo.core.admin.service.AlbumService;
import com.light.hexo.core.admin.service.ConfigService;
import com.light.hexo.core.portal.model.HexoPageInfo;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.mapper.AlbumDetailMapper;
import com.light.hexo.mapper.model.Album;
import com.light.hexo.mapper.model.AlbumDetail;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
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

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private BlogConfig blogConfig;

    @Autowired
    private Environment environment;

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
        example.orderBy("sort").asc();
        PageHelper.startPage(pageNum, pageSize);
        return this.albumDetailMapper.selectByExample(example);
    }

    @Override
    public void saveAlbumDetail(Integer albumId, String originalName, String url, String coverUrl) throws GlobalException {
        AlbumDetail albumDetail = new AlbumDetail();
        albumDetail.setAlbumId(albumId)
                   .setName(FilenameUtils.getBaseName(originalName))
                   .setUrl(url)
                   .setCoverUrl(coverUrl)
                   .setSort(0)
                   .setCreateTime(LocalDateTime.now())
                   .setUpdateTime(albumDetail.getCreateTime());
        super.saveModel(albumDetail);
    }

    @SneakyThrows
    @Override
    public void saveAlbumDetail(AlbumDetail albumDetail) {
        Album album = this.albumService.findById(albumDetail.getAlbumId());
        if (album == null) {
            return;
        }

        String baseName = FilenameUtils.getBaseName(albumDetail.getName());
        String coverUrl = "";

        if (album.getDetailType().equals(2)) {
            String coverBase64 = albumDetail.getCoverUrl();
            if (StringUtils.isNotBlank(coverBase64)) {
                // 解密
                Base64.Decoder decoder = Base64.getDecoder();

                coverBase64 = coverBase64.substring(coverBase64.indexOf(",", 1) + 1, coverBase64.length());
                byte[] data = decoder.decode(coverBase64);
                // 处理数据
                for (int i = 0; i < data.length; ++i) {
                    if (data[i] < 0) {
                        data[i] += 256;
                    }
                }

                String filePath = this.configService.getConfigValue(ConfigEnum.LOCAL_FILE_PATH.getName());
                String localFilePath = StringUtils.isNotBlank(filePath) ? filePath  + File.separator : this.blogConfig.getAttachmentDir();
                String coverName = baseName + "_" + RandomUtil.randomNumbers(6) + ".jpg";
                File parent = new File(localFilePath + "/cover/");
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                IOUtils.write(data, new FileOutputStream(new File(parent.getAbsolutePath(), coverName)));

                String blogPage = this.configService.getConfigValue(ConfigEnum.HOME_PAGE.getName());
                coverUrl = (StringUtils.isNotBlank(blogPage) ? blogPage : "http://" + RequestUtil.getHostIp() + ":" + this.environment.getProperty("server.port")) + "/cover/" + coverName;
            }
        } else {
            // 图片封面地址为图片本身
            coverUrl = albumDetail.getUrl();
        }

        albumDetail.setCoverUrl(coverUrl);
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
