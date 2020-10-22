package com.light.hexo.business.admin.service.impl;

import com.light.hexo.business.admin.mapper.FriendLinkMapper;
import com.light.hexo.business.admin.model.FriendLink;
import com.light.hexo.business.admin.model.Tag;
import com.light.hexo.business.admin.service.FriendLinkService;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.FriendLinkRequest;
import com.light.hexo.common.util.CacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: FriendLinkServiceImpl
 * @ProjectName hexo-boot
 * @Description: 友链 Service 实现
 * @DateTime 2020/9/22 17:47
 */
@Service
public class FriendLinkServiceImpl extends BaseServiceImpl<FriendLink> implements FriendLinkService {

    @Autowired
    private FriendLinkMapper friendLinkMapper;

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
        CacheUtil.remove(CacheKey.FRIEND_LINK_LIST);
    }

    @Override
    public void updateFriendLink(FriendLink friendLink) throws GlobalException {
        super.updateModel(friendLink);
        // 清除缓存
        CacheUtil.remove(CacheKey.FRIEND_LINK_LIST);
    }

    @Override
    public void removeFriendLinkBatch(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());
        Example example = new Example(Tag.class);
        example.createCriteria().andIn("id", idList);
        this.getBaseMapper().deleteByExample(example);
        // 清除缓存
        CacheUtil.remove(CacheKey.FRIEND_LINK_LIST);
    }

    @Override
    public List<FriendLink> listFriendLinkByIndex() throws GlobalException {
        String cacheKey = CacheKey.FRIEND_LINK_LIST;
        List<FriendLink> friendLinkList = CacheUtil.get(cacheKey);
        if (CollectionUtils.isEmpty(friendLinkList)) {
            friendLinkList = super.findAll();
            CacheUtil.put(cacheKey, friendLinkList);
        }

        return friendLinkList;
    }

    @Override
    public Integer getFriendLinkNum() throws GlobalException {
        Example example = new Example(FriendLink.class);
        return this.getBaseMapper().selectCountByExample(example);
    }
}
