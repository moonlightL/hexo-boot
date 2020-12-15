package com.light.hexo.business.admin.service.impl;

import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.constant.StateEnum;
import com.light.hexo.business.admin.mapper.NavMapper;
import com.light.hexo.business.admin.model.Nav;
import com.light.hexo.business.admin.service.NavService;
import com.light.hexo.business.portal.constant.PageConstant;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.NavRequest;
import com.light.hexo.common.util.EhcacheUtil;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: NavServiceImpl
 * @ProjectName hexo-boot
 * @Description: 导航 Service 实现
 * @DateTime 2020/12/14 17:33
 */
@CacheConfig(cacheNames = "navCache")
@Service
public class NavServiceImpl extends BaseServiceImpl<Nav> implements NavService {

    @Autowired
    private NavMapper navMapper;

    @Override
    public BaseMapper<Nav> getBaseMapper() {
        return this.navMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {

        NavRequest navRequest = (NavRequest) request;
        Example example = Example.builder(Nav.class)
                .select("id", "name", "link", "code", "icon", "navType", "sort", "state")
                .orderBy("sort")
                .build();
        Example.Criteria criteria = example.createCriteria();

        String name = navRequest.getName();
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", name.trim() + "%");
        }

        Boolean state = navRequest.getState();
        if (state != null) {
            criteria.andEqualTo("state", state);
        }

        return example;
    }

    @Override
    public Nav findByLink(String link) throws GlobalException {

        Example example = new Example(Nav.class);
        example.createCriteria().andEqualTo("link", "/custom/" + link);
        Nav nav = this.getBaseMapper().selectOneByExample(example);

        if (nav == null || StateEnum.OFF.getCode().equals(nav.getState())) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_NAV_NOT_EXIST);
        }

        return nav;
    }

    @Cacheable(key = "'" + PageConstant.NAV_LIST + "'")
    @Override
    public List<Nav> listNavsByIndex() throws GlobalException {

        Example example = Example.builder(Nav.class)
                .select("id", "name", "link", "code", "icon")
                .orderBy("sort")
                .build();

        example.createCriteria().andEqualTo("state", 1);

        return this.getBaseMapper().selectByExample(example);
    }

    @Override
    public void removeNavBatch(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());

        this.removeBatch(idList);
        EhcacheUtil.clearByCacheName("navCache");
    }

    @Override
    public void saveNav(Nav nav) throws GlobalException {
        String link = nav.getLink();
        if (!link.startsWith("/custom/")) {
            nav.setLink("/custom/" + link);
        }
        nav.setNavType(2);
        super.saveModel(nav);
        EhcacheUtil.clearByCacheName("navCache");
    }

    @Override
    public void updateNav(Nav nav) throws GlobalException {
        String link = nav.getLink();
        if (!link.startsWith("/custom/")) {
            nav.setLink("/custom/" + link);
        }
        super.updateModel(nav);
        EhcacheUtil.clearByCacheName("navCache");
    }

    @Override
    public int updateModel(Nav model) throws GlobalException {
        int num = super.updateModel(model);
        if (num > 0) {
            EhcacheUtil.clearByCacheName("navCache");
        }
        return num;
    }
}
