package com.light.hexo.business.admin.service.impl;

import com.light.hexo.business.admin.mapper.TagMapper;
import com.light.hexo.business.admin.model.Tag;
import com.light.hexo.business.admin.model.event.TagEvent;
import com.light.hexo.business.admin.service.TagService;
import com.light.hexo.business.portal.constant.PageConstant;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.TagRequest;
import com.light.hexo.common.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import tk.mybatis.mapper.entity.Example;
import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: TagServiceImpl
 * @ProjectName hexo-boot
 * @Description: 标签 Service 实现
 * @DateTime 2020/8/3 16:30
 */
@CacheConfig(cacheNames = "tagCache")
@Service
@Slf4j
public class TagServiceImpl extends BaseServiceImpl<Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    @Lazy
    private EventPublisher eventPublisher;

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
        // 清除缓存
        this.eventPublisher.emit(new TagEvent());
    }

    @Override
    public List<Integer> saveTagBatch(String[] tagNames) throws GlobalException {
        // 查询旧标签
        List<Tag> oldTagList = this.tagMapper.selectListByTags(tagNames);
        List<String> oldTagNameList = oldTagList.stream().map(i -> i.getName().toLowerCase()).collect(Collectors.toList());

        // 过滤出新标签
        List<String> tmpTagNameList = Arrays.stream(tagNames).filter(i -> !oldTagNameList.contains(i.toLowerCase())).collect(Collectors.toList());

        List<Tag> newTagList = new ArrayList<>(tmpTagNameList.size());
        for (String tagName : tmpTagNameList) {
            Tag tag = new Tag();
            tag.setName(tagName).setCreateTime(LocalDateTime.now()).setUpdateTime(tag.getCreateTime());
            newTagList.add(tag);
        }

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

    @Cacheable(key = "'" + PageConstant.TAG_LIST + "'")
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

    @Override
    public EventEnum getEventType() {
        return EventEnum.TAG;
    }

    @Override
    public void dealWithEvent(BaseEvent event) {

        WebApplicationContext webApplicationContext = (WebApplicationContext) SpringContextUtil.applicationContext;
        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext == null) {
            log.info("===========TagService dealWithEvent 获取 servletContext 为空============");
            return;
        }

        servletContext.setAttribute("tagNum", this.getTagNum());
    }
}
