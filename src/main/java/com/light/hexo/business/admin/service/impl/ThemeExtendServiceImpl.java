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
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public void saveThemeExtend(Integer themeId, Map<String, Object> extensionMap) throws GlobalException {

        if (CollectionUtils.isEmpty(extensionMap)) {
            return;
        }

        List<ThemeExtend> list = new ArrayList<>();
        extensionMap.forEach((k, v) -> {
            ThemeExtend themeExtend = new ThemeExtend();
            themeExtend.setConfigName(k)
                       .setConfigValue(v.toString())
                       .setThemeId(themeId)
                       .setCreateTime(LocalDateTime.now())
                       .setUpdateTime(themeExtend.getCreateTime());
            list.add(themeExtend);
        });

        this.themeExtendMapper.updateByConfigName(list);
    }
}
