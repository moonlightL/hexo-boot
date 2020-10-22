package com.light.hexo.business.admin.service.impl;

import com.light.hexo.business.admin.mapper.PostTagMapper;
import com.light.hexo.business.admin.model.PostTag;
import com.light.hexo.business.admin.service.PostTagService;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.exception.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: PostTagServiceImpl
 * @ProjectName hexo-boot
 * @Description: 文章标签 Service 实现
 * @DateTime 2020/9/17 15:38
 */
@Service
public class PostTagServiceImpl extends BaseServiceImpl<PostTag> implements PostTagService {

    @Autowired
    private PostTagMapper postTagMapper;

    @Override
    public BaseMapper<PostTag> getBaseMapper() {
        return this.postTagMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        return null;
    }

    @Override
    public List<String> listTagNamesByPostId(Integer postId) throws GlobalException {
        return this.postTagMapper.selectTagNameByPostId(postId);
    }

    @Override
    public void savePostTagBatch(List<PostTag> list) throws GlobalException {
        this.postTagMapper.insertPostTagBatch(list);
    }

    @Override
    public void deletePostTag(Integer postId) throws GlobalException {
        this.postTagMapper.deleteByPostId(postId);
    }


}
