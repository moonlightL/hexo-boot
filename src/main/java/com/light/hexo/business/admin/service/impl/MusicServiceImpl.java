package com.light.hexo.business.admin.service.impl;

import com.light.hexo.business.admin.mapper.MusicMapper;
import com.light.hexo.business.admin.model.Music;
import com.light.hexo.business.admin.service.MusicService;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.model.MusicRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: MusicServiceImpl
 * @ProjectName hexo-boot
 * @Description: 音乐 Service 实现
 * @DateTime 2021/2/3 15:47
 */
@Service
public class MusicServiceImpl extends BaseServiceImpl<Music> implements MusicService {

    @Autowired
    private MusicMapper musicMapper;

    @Override
    public BaseMapper<Music> getBaseMapper() {
        return this.musicMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        MusicRequest musicRequest = (MusicRequest) request;
        Example example = new Example(Music.class);
        Example.Criteria criteria = example.createCriteria();

        String name = musicRequest.getName();
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", name.trim() + "%");
        }

        String artist = musicRequest.getArtist();
        if (StringUtils.isNotBlank(artist)) {
            criteria.andLike("artist", artist.trim() + "%");
        }

        example.orderBy("sort").asc();
        return example;
    }

    @Override
    public void removeMusicBatch(List<String> idStrList) {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());
        Example example = new Example(Music.class);
        example.createCriteria().andIn("id", idList);
        this.getBaseMapper().deleteByExample(example);
    }

    @Override
    public List<Music> listMusicByIndex() {
        Example example = new Example(Music.class);
        example.createCriteria().andEqualTo("state", true);
        example.orderBy("sort").asc();
        return this.getBaseMapper().selectByExample(example);
    }
}
