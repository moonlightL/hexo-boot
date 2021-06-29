package com.light.hexo.business.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.DynamicMapper;
import com.light.hexo.business.admin.model.Dynamic;
import com.light.hexo.business.admin.model.event.DynamicEvent;
import com.light.hexo.business.admin.service.DynamicService;
import com.light.hexo.business.portal.constant.PageConstant;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.DynamicRequest;
import com.light.hexo.common.util.CacheUtil;
import com.light.hexo.common.util.DateUtil;
import com.light.hexo.common.util.EhcacheUtil;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: DynamicServiceImpl
 * @ProjectName hexo-boot
 * @Description: 动态 Service 实现
 * @DateTime 2021/6/23 14:57
 */
@CacheConfig(cacheNames = "dynamicCache")
@Service
public class DynamicServiceImpl extends BaseServiceImpl<Dynamic> implements DynamicService {

    @Autowired
    private DynamicMapper dynamicMapper;

    @Autowired
    @Lazy
    private EventPublisher eventPublisher;

    @Override
    public BaseMapper<Dynamic> getBaseMapper() {
        return this.dynamicMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {

        DynamicRequest dynamicRequest = (DynamicRequest) request;

        Example example = new Example(Dynamic.class);
        example.orderBy("createTime").desc();
        return example;
    }

    @Override
    public void saveDynamic(Dynamic dynamic) throws GlobalException {
        if (StringUtils.isBlank(dynamic.getColor())) {
            dynamic.setColor("#2ECC71");
        }

        Document document = Jsoup.parse(dynamic.getContent());
        // 转换 img
        Elements imgElements = document.select("img");
        for (Element img : imgElements) {
            String src = img.attr("src");
            img.attr("data-original", src);
            img.wrap("<a class='fancybox' href='" + src + "' data-fancybox='gallery'></a>");
        }

        dynamic.setContent(document.toString());
        this.saveModel(dynamic);
        EhcacheUtil.clearByCacheName("dynamicCache");
    }

    @Override
    public void updateDynamic(Dynamic dynamic) throws GlobalException {

        Dynamic dbRecord = this.findById(dynamic.getId());
        if (dbRecord == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_DYNAMIC_NOT_EXIST);
        }

        Document document = Jsoup.parse(dynamic.getContent());
        // 转换 img
        Elements imgElements = document.select("img");
        for (Element img : imgElements) {
            Element parent = img.parent();
            String aClass = parent.attr("class");
            if ("fancybox".equals(aClass)) {
                continue;
            }
            String src = img.attr("src");
            img.attr("data-original", src);
            img.wrap("<a class='fancybox' href='" + src + "' data-fancybox='gallery'></a>");
        }

        dynamic.setContent(document.toString());

        this.updateModel(dynamic);
        EhcacheUtil.clearByCacheName("dynamicCache");
    }

    @Override
    public void removeDynamicBatch(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());
        // 逻辑删除
        this.removeBatch(idList);
        EhcacheUtil.clearByCacheName("dynamicCache");
    }

    @Cacheable(key = "'" + PageConstant.DYNAMIC_LIST + ":' + #pageNum")
    @Override
    public List<Dynamic> listDynamicByIndex(Integer pageNum, int pageSize) {
        Example example = new Example(Dynamic.class);
        example.orderBy("createTime").desc();
        PageHelper.startPage(pageNum, pageSize);
        List<Dynamic> dynamicList = this.getBaseMapper().selectByExample(example);
        for (Dynamic dynamic : dynamicList) {
            dynamic.setTimeDesc(DateUtil.timeDesc(dynamic.getCreateTime()));
        }

        return dynamicList;
    }

    @Override
    public int praiseDynamic(String ipAddr, Integer dynamicId) {
        String cacheKey = "prizeDynamicCache" + ipAddr + ":" + dynamicId;
        Object obj = CacheUtil.get(cacheKey);
        if (obj != null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_REPEAT_PRAISE_POST);
        }

        Dynamic dynamic = super.findById(dynamicId);
        if (dynamic == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_DYNAMIC_NOT_EXIST);
        }

        CacheUtil.put(cacheKey, dynamicId, 60 * 1000);
        this.eventPublisher.emit(new DynamicEvent(dynamicId));
        return dynamic.getPraiseNum() + 1;
    }

    @Override
    public EventEnum getEventType() {
        return EventEnum.DYNAMIC;
    }

    @Override
    public void dealWithEvent(BaseEvent event) {
        DynamicEvent dynamicEvent = (DynamicEvent) event;
        Dynamic dynamic = this.findById(dynamicEvent.getId());
        if (dynamic == null) {
            return;
        }

        Dynamic data = new Dynamic();
        data.setId(dynamic.getId())
            .setPraiseNum(dynamic.getPraiseNum() + 1)
            .setUpdateTime(LocalDateTime.now());
        this.updateDynamic(data);
    }
}
