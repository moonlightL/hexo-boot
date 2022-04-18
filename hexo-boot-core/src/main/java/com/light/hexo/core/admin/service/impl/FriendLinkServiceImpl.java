package com.light.hexo.core.admin.service.impl;

import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.mapper.mapper.FriendLinkMapper;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.model.FriendLink;
import com.light.hexo.mapper.model.Tag;
import com.light.hexo.common.event.FriendLinkEvent;
import com.light.hexo.core.admin.service.FriendLinkService;
import com.light.hexo.core.portal.constant.PageConstant;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.request.FriendLinkRequest;
import com.light.hexo.common.util.EhcacheUtil;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: FriendLinkServiceImpl
 * @ProjectName hexo-boot
 * @Description: 友链 Service 实现
 * @DateTime 2020/9/22 17:47
 */
@CacheConfig(cacheNames = "friendLinkCache")
@Service
@Slf4j
public class FriendLinkServiceImpl extends BaseServiceImpl<FriendLink> implements FriendLinkService {

    @Autowired
    private FriendLinkMapper friendLinkMapper;

    @Autowired
    @Lazy
    private EventPublisher eventPublisher;

    @Override
    public BaseMapper<FriendLink> getBaseMapper() {
        return this.friendLinkMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {

        FriendLinkRequest friendLinkRequest = (FriendLinkRequest) request;
        Example example = new Example(FriendLink.class);
        Example.Criteria criteria = example.createCriteria();

        String title = friendLinkRequest.getTitle();
        if (StringUtils.isNotBlank(title)) {
            criteria.andLike("title", title.trim() + "%");
        }

        example.orderBy("id").desc();

        return example;
    }

    @Override
    public void saveFriendLink(FriendLink friendLink) throws GlobalException {
        super.saveModel(friendLink);
        // 清除缓存
        this.eventPublisher.emit(new FriendLinkEvent());
    }

    @Override
    public void updateFriendLink(FriendLink friendLink) throws GlobalException {
        super.updateModel(friendLink);
        // 清除缓存
        this.eventPublisher.emit(new FriendLinkEvent());
    }

    @Override
    public void removeFriendLinkBatch(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());
        Example example = new Example(Tag.class);
        example.createCriteria().andIn("id", idList);
        this.getBaseMapper().deleteByExample(example);
        // 清除缓存
        this.eventPublisher.emit(new FriendLinkEvent());
    }

    @Cacheable(key = "'" + PageConstant.FRIEND_LINK_LIST + "'")
    @Override
    public List<FriendLink> listFriendLinkByIndex() throws GlobalException {
        Example example = new Example(FriendLink.class);
        example.orderBy("sort").asc();
        return this.getBaseMapper().selectByExample(example);
    }

    @Override
    public Integer getFriendLinkNum() throws GlobalException {
        Example example = new Example(FriendLink.class);
        return this.getBaseMapper().selectCountByExample(example);
    }

    @Override
    public EventEnum getEventType() {
        return EventEnum.FRIEND_LINK;
    }

    @Override
    public void dealWithEvent(BaseEvent event) {

        EhcacheUtil.clearByCacheName("friendLinkCache");

        WebApplicationContext webApplicationContext = (WebApplicationContext) SpringContextUtil.applicationContext;
        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext == null) {
            log.info("===========FriendService dealWithEvent 获取 servletContext 为空============");
            return;
        }

        List<FriendLink> friendLinkList = this.listFriendLinkByIndex();
        servletContext.setAttribute("friendLinkList", friendLinkList);
        servletContext.setAttribute("friendLinkNum", friendLinkList.size());
    }
}
