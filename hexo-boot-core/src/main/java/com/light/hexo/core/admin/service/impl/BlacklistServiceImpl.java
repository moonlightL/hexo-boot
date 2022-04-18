package com.light.hexo.core.admin.service.impl;

import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.mapper.mapper.BlacklistMapper;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.model.Blacklist;
import com.light.hexo.core.admin.service.BlacklistService;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.request.BlackListRequest;
import com.light.hexo.common.util.CacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: BlacklistServiceImpl
 * @ProjectName hexo-boot
 * @Description: 黑名单 Service 实现
 * @DateTime 2020/9/9 15:35
 */
@Service
public class BlacklistServiceImpl extends BaseServiceImpl<Blacklist> implements BlacklistService {

    @Autowired
    private BlacklistMapper blacklistMapper;

    @Override
    public BaseMapper<Blacklist> getBaseMapper() {
        return this.blacklistMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {

        BlackListRequest blackRequest = (BlackListRequest) request;

        Example example = new Example(Blacklist.class);
        Example.Criteria criteria = example.createCriteria();
        String ipAddress = blackRequest.getIpAddress();
        if (StringUtils.isNotBlank(ipAddress)) {
            criteria.andLike("ipAddress", ipAddress.trim() + "%");
        }

        return example;
    }

    @Override
    public int removeBatch(List<? extends Serializable> idList) throws GlobalException {
        int result = super.removeBatch(idList);
        CacheUtil.remove(CacheKey.BLACK_LIST);
        return result;
    }

    @Override
    public int saveModel(Blacklist model) throws GlobalException {
        int result = super.saveModel(model);
        CacheUtil.remove(CacheKey.BLACK_LIST);
        return result;
    }

    @Override
    public int updateModel(Blacklist model) throws GlobalException {
        int result = super.updateModel(model);
        CacheUtil.remove(CacheKey.BLACK_LIST);
        return result;
    }

    @Override
    public boolean isBlacklist(String ip) throws GlobalException {
        String cacheKey = CacheKey.BLACK_LIST;
        List<Blacklist> blacklistList = CacheUtil.get(cacheKey);
        // 此处只做 null ，不做 isEmpty 判断，避免没有黑名单数据下访问一直查询数据库
        if (blacklistList == null) {
            blacklistList = super.findAll();
            CacheUtil.put(cacheKey, blacklistList);
        }

        List<String> ipList = blacklistList.stream().map(Blacklist::getIpAddress).collect(Collectors.toList());
        return ipList.contains(ip);
    }

    @Override
    @OperateLog(value = "操作异常频繁", actionType = ActionEnum.ADMIN_ADD)
    public void saveBlacklist(String ipAddr, String remark, Integer hour) throws GlobalException {
        Blacklist blacklist = new Blacklist();
        blacklist.setIpAddress(ipAddr).setRemark(remark);
        if (hour != null) {
            blacklist.setState(2).setExpireTime(LocalDateTime.now().plusHours(hour));
        } else {
            blacklist.setState(1);
        }
        super.saveModel(blacklist);
        CacheUtil.remove(CacheKey.BLACK_LIST);
    }

    @Override
    public List<Blacklist> listExpireBlacks(LocalDateTime dateTime) throws GlobalException {
        Example example = new Example(Blacklist.class);
        example.createCriteria()
               .andEqualTo("state", 2)
               .andLessThanOrEqualTo("expireTime", dateTime);
        return this.getBaseMapper().selectByExample(example);
    }

}