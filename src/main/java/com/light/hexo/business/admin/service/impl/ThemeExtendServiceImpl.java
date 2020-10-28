package com.light.hexo.business.admin.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.mapper.ThemeExtendMapper;
import com.light.hexo.business.admin.model.Config;
import com.light.hexo.business.admin.model.ThemeExtend;
import com.light.hexo.business.admin.service.ThemeExtendService;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.util.CacheUtil;
import com.light.hexo.common.util.EhcacheUtil;
import com.light.hexo.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author MoonlightL
 * @ClassName: ThemeExtendServiceImpl
 * @ProjectName hexo-boot
 * @Description: 主题配置扩展 Service 实现
 * @DateTime 2020/10/27 17:04
 */
@Service
@Slf4j
public class ThemeExtendServiceImpl extends BaseServiceImpl<ThemeExtend> implements ThemeExtendService {

    @Autowired
    private ThemeExtendMapper themeExtendMapper;

    @Override
    public BaseMapper<ThemeExtend> getBaseMapper() {
        return this.themeExtendMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        return null;
    }

    @Override
    public void saveThemeExtend(Integer themeId, List<Map<String, String>> extension) throws GlobalException {

        if (CollectionUtils.isEmpty(extension)) {
            return;
        }

        List<ThemeExtend> list = new ArrayList<>();

        for (Map<String, String> objectMap : extension) {
            ThemeExtend themeExtend = new ThemeExtend();
            themeExtend.setConfigName(objectMap.get("key"))
                       .setConfigValue(objectMap.get("value"))
                       .setConfigType(objectMap.get("type"))
                       .setConfigLabel(objectMap.get("label"))
                       .setConfigOption(Objects.nonNull(objectMap.get("option")) ? objectMap.get("option") : "")
                       .setThemeId(themeId)
                       .setCreateTime(LocalDateTime.now())
                       .setUpdateTime(themeExtend.getCreateTime());
            list.add(themeExtend);
        }

        this.themeExtendMapper.updateBatchByConfigName(list);
    }

    @Override
    public void saveThemeExtend(List<ThemeExtend> themeExtendList) throws GlobalException {
        if (CollectionUtils.isEmpty(themeExtendList)) {
            return;
        }

        this.themeExtendMapper.updateBatchById(themeExtendList);
        CacheUtil.remove(CacheKey.CURRENT_THEME);
        EhcacheUtil.clearByCacheName("postCache");
    }

    @Override
    public List<ThemeExtend> listThemeExtends(Integer themeId) throws GlobalException {

        Example example = new Example(ThemeExtend.class);
        example.createCriteria().andEqualTo("themeId", themeId);

        return this.getBaseMapper().selectByExample(example);
    }

    @Override
    public Map<String, String> getThemeExtendMap(Integer themeId) throws GlobalException {

        Map<String, String> result = new HashMap<>();
        List<ThemeExtend> extendList = this.listThemeExtends(themeId);
        if (CollectionUtils.isEmpty(extendList)) {
            return result;
        }

        for (ThemeExtend themeExtend : extendList) {
            result.put(themeExtend.getConfigName(), themeExtend.getConfigValue());
        }

        return result;
    }
}
