package com.light.hexo.business.admin.service.impl;

import com.light.hexo.business.admin.mapper.TagMapper;
import com.light.hexo.business.admin.model.Tag;
import com.light.hexo.business.admin.service.TagService;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.TagRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author lgw
 * @ClassName: TagServiceImpl
 * @ProjectName hexo-boot
 * @Description: 标签 Service 实现
 * @DateTime 2020/8/3 16:30
 */
@Service
public class TagServiceImpl extends BaseServiceImpl<Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public BaseMapper<Tag> getBaseMapper() {
        return this.tagMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        // 获取查询参数
        TagRequest tagRequest = (TagRequest) request;
        Example example = new Example(Tag.class);
        Example.Criteria criteria = example.createCriteria();

        String name = tagRequest.getName();
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", name.trim() + "%");
        }

        example.orderBy("id").desc();

        return example;
    }

    @Override
    public void removeTagBatch(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());
        Example example = new Example(Tag.class);
        example.createCriteria().andIn("id", idList);
        this.getBaseMapper().deleteByExample(example);
    }

    @Override
    public List<Integer> saveTagBatch(String[] tagNames) throws GlobalException {
        Tag tag;
        List<Tag> list = new ArrayList<>(tagNames.length);
        for (String tagName : tagNames) {
            tag = new Tag();
            tag.setName(tagName).setCreateTime(LocalDateTime.now()).setUpdateTime(tag.getCreateTime());
            list.add(tag);
        }

        List<Tag> oldTagList = this.tagMapper.selectListByTags(tagNames);
        List<String> oldTagNameList = oldTagList.stream().map(Tag::getName).collect(Collectors.toList());

        List<Tag> newTagList = list.stream().filter(i -> !oldTagNameList.contains(i.getName())).collect(Collectors.toList());
        if (!newTagList.isEmpty()) {
            // 保存新标签
            this.tagMapper.insertBatch(newTagList);
        }

        List<Integer> result = oldTagList.stream().map(Tag::getId).collect(Collectors.toList());
        List<Integer> newIdList = newTagList.stream().map(Tag::getId).collect(Collectors.toList());
        result.addAll(newIdList);
        return result;
    }

    @Override
    public Integer getTagNum() throws GlobalException {
        return this.getBaseMapper().selectCount(null);
    }

    @Override
    public List<Tag> listTagsByIndex() throws GlobalException {
        List<Tag> list = super.findAll();
        for (Tag tag : list) {
            tag.setStyle("font-size: 12px; color: #ccc");
            tag.setUrl("/tags/" + tag.getName() + "/");
        }
        return list;
    }

    @Override
    public Tag findByTagName(String tagName) throws GlobalException {

        Example example = new Example(Tag.class);
        example.createCriteria().andEqualTo("name", tagName);

        return this.getBaseMapper().selectOneByExample(example);
    }
}
