package com.light.hexo.business.admin.service.impl;

import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.NavMapper;
import com.light.hexo.business.admin.model.Nav;
import com.light.hexo.business.admin.model.event.NavEvent;
import com.light.hexo.business.admin.service.NavService;
import com.light.hexo.business.portal.constant.PageConstant;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.NavRequest;
import com.light.hexo.common.util.CacheUtil;
import com.light.hexo.common.util.EhcacheUtil;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;
import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
@Slf4j
public class NavServiceImpl extends BaseServiceImpl<Nav> implements NavService {

    @Autowired
    private NavMapper navMapper;

    @Autowired
    @Lazy
    private EventPublisher eventPublisher;

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
        List<Nav> navList = this.listNavs();
        Optional<Nav> first = navList.stream().filter(i -> i.getLink().equals(link)).findFirst();
        Nav nav = first.orElseGet(() -> new Nav("tmp", "/tmp", "", "tmp"));
        this.eventPublisher.emit(new NavEvent(nav.getId(), NavEvent.Type.READ));
        return nav;
    }

    /**
     * 此方法内部调用，@Cacheable 注解会失效，故用 CacheUtil 替代
     * @return
     * @throws GlobalException
     */
    @Override
    public List<Nav> listNavs() throws GlobalException {
        List<Nav> navList = CacheUtil.get(CacheKey.NAV_LIST);
        if (CollectionUtils.isEmpty(navList)) {
            Example example = Example.builder(Nav.class)
                    .select("id", "name", "link", "code", "icon", "cover", "parentId")
                    .where(Sqls.custom().andEqualTo("state", 1))
                    .orderByAsc("sort").build();
            navList = this.getBaseMapper().selectByExample(example);
            CacheUtil.put(CacheKey.NAV_LIST, navList);
        }

        return navList;
    }

    @Override
    public void removeNavBatch(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());

        this.removeBatch(idList);
        EhcacheUtil.clearByCacheName("navCache");
        CacheUtil.remove(CacheKey.NAV_LIST);
        this.eventPublisher.emit(new NavEvent(NavEvent.Type.LOAD));
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
        CacheUtil.remove(CacheKey.NAV_LIST);
        this.eventPublisher.emit(new NavEvent(NavEvent.Type.LOAD));
    }

    @Override
    public void updateNav(Nav nav) throws GlobalException {
        Nav dbNav = this.findById(nav.getId());
        if (dbNav == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_NAV_NOT_EXIST);
        }

        String link = nav.getLink();
        if (!link.startsWith("/custom/")) {
            nav.setLink("/custom/" + link);
        }

        // 默认导航不能修改链接
        if (dbNav.getNavType().equals(1)) {
            nav.setLink(null);
        }

        super.updateModel(nav);
        EhcacheUtil.clearByCacheName("navCache");
        CacheUtil.remove(CacheKey.NAV_LIST);
        this.eventPublisher.emit(new NavEvent(NavEvent.Type.LOAD));
    }

    @Override
    public List<Nav> listParentNav() throws GlobalException {
        Example example = new Example(Nav.class);
        example.createCriteria().andEqualTo("parentId", 0)
                .andEqualTo("state", 1);
        example.orderBy("sort").asc();
        return this.getBaseMapper().selectByExample(example);
    }

    @Override
    public int updateModel(Nav model) throws GlobalException {
        int num = super.updateModel(model);
        if (num > 0) {
            EhcacheUtil.clearByCacheName("navCache");
            CacheUtil.remove(CacheKey.NAV_LIST);
            this.eventPublisher.emit(new NavEvent(NavEvent.Type.LOAD));
        }
        return num;
    }

    @Override
    public void initNav(ServletContext servletContext) {
        List<Nav> navList = this.listNavs();
        List<Nav> firstNav = navList.stream().filter(i -> i.getParentId().equals(0)).collect(Collectors.toList());
        Map<Integer, List<Nav>> childrenMap = navList.stream().collect(Collectors.groupingBy(Nav::getParentId));
        for (Nav parent : firstNav) {
            parent.setChildren(childrenMap.get(parent.getId()));
        }

        // 用于前端导航显示
        servletContext.setAttribute("firstNav", firstNav);
    }

    @Cacheable(key = "'" + PageConstant.NAV_PAGE + ":' + #link")
    @Override
    public Nav findCustomLink(String link) throws GlobalException {
        Example example = new Example(Nav.class);
        example.createCriteria().andEqualTo("link", "/custom/" + link);
        Nav nav = this.getBaseMapper().selectOneByExample(example);
        if (nav == null || !nav.getState()) {
            ExceptionUtil.throwExToPage(HexoExceptionEnum.ERROR_NAV_PAGE_NOT_EXIST);
        }
        return nav;
    }

    @Override
    public EventEnum getEventType() {
        return EventEnum.NAV;
    }

    @Override
    public void dealWithEvent(BaseEvent event) {

        NavEvent navEvent = (NavEvent) event;

        if (navEvent.getId() == null) {
            return;
        }

        if (NavEvent.Type.LOAD.getCode().equals(navEvent.getType().getCode())) {
            WebApplicationContext webApplicationContext = (WebApplicationContext) SpringContextUtil.applicationContext;
            ServletContext servletContext = webApplicationContext.getServletContext();
            if (servletContext == null) {
                log.info("===========NavService dealWithEvent 获取 servletContext 为空============");
                return;
            }
            this.initNav(servletContext);

        } else if (NavEvent.Type.READ.getCode().equals(navEvent.getType().getCode())) {
            Nav nav = this.findById(navEvent.getId());
            if (nav != null) {
                Nav tmp = new Nav();
                tmp.setId(nav.getId()).setReadNum(nav.getReadNum() + 1).setUpdateTime(LocalDateTime.now());
                super.updateModel(tmp);
            }
        }
    }

}
