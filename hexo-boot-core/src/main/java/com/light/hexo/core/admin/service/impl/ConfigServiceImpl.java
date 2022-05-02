package com.light.hexo.core.admin.service.impl;

import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.constant.ConfigEnum;
import com.light.hexo.mapper.mapper.ConfigMapper;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.model.Config;
import com.light.hexo.common.component.event.ConfigEvent;
import com.light.hexo.core.admin.service.ConfigService;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.util.CacheUtil;
import com.light.hexo.common.util.EhcacheUtil;
import com.light.hexo.common.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: ConfigServiceImpl
 * @ProjectName hexo-boot
 * @Description: 配置 Service 实现
 * @DateTime 2020/9/9 11:17
 */
@Service
@Slf4j
public class ConfigServiceImpl extends BaseServiceImpl<Config> implements ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    @Lazy
    private EventPublisher eventPublisher;

    @Override
    public BaseMapper<Config> getBaseMapper() {
        return this.configMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        return null;
    }


    @Override
    public boolean saveConfig(Map<String, String> paramMap) throws GlobalException {
        if (paramMap.isEmpty()) {
            return true;
        }

        List<Config> configList = new ArrayList<>(paramMap.size());

        ConfigEnum[] configEnums = ConfigEnum.values();
        List<String> nameList = Arrays.stream(configEnums).map(ConfigEnum::getName).collect(Collectors.toList());

        paramMap.forEach((key, value) -> {
            if (nameList.contains(key)) {
                Config config = new Config();
                config.setConfigKey(key)
                      .setConfigValue(value.trim())
                      .setRemark(ConfigEnum.valueOf(key.toUpperCase()).getRemark())
                      .setCreateTime(LocalDateTime.now())
                      .setUpdateTime(config.getCreateTime());
                configList.add(config);
            }
        });

        this.configMapper.updateByConfigKey(configList);
        CacheUtil.remove(CacheKey.CONFIG_LIST);
        this.eventPublisher.emit(new ConfigEvent(this));

        EhcacheUtil.clearByCacheName("postCache");

        return true;
    }

    @Override
    public Map<String, String> getConfigMap() throws GlobalException {

        String cacheKey = CacheKey.CONFIG_LIST;
        List<Config> configList = CacheUtil.get(cacheKey);
        if (CollectionUtils.isEmpty(configList)) {
            configList = super.findAll();
            CacheUtil.put(cacheKey, configList);
        }

        if (configList.isEmpty()) {
            return new HashMap<>();
        }

        return configList.stream().collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue,  (v1, v2) -> v2));
    }

    @Override
    public String getConfigValue(String configKey) throws GlobalException {

        Map<String, String> configMap = this.getConfigMap();
        if (CollectionUtils.isEmpty(configMap)) {
            return "";
        }

        return configMap.get(configKey);
    }

    @Override
    public void saveConfigBatch(List<Config> configList) throws GlobalException {
        this.configMapper.updateByConfigKey(configList);
        CacheUtil.remove(CacheKey.CONFIG_LIST);
        this.eventPublisher.emit(new ConfigEvent(this));
    }

    @Override
    public String getCode() {
        return EventEnum.CONFIG.getType();
    }

    @Override
    public void dealWithEvent(BaseEvent event) {

        WebApplicationContext webApplicationContext = (WebApplicationContext) SpringContextUtil.applicationContext;
        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext == null) {
            log.info("===========ConfigService dealWithEvent 获取 servletContext 为空============");
            return;
        }

        List<Config> configList = super.findAll();
        Map<String, String> configMap = configList.stream().collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue,  (v1, v2) -> v2));
        servletContext.setAttribute("configMap", configMap);
    }
}
