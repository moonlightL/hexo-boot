package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.Config;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.exception.GlobalException;

import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: ConfigService
 * @ProjectName hexo-boot
 * @Description: 配置 Service
 * @DateTime 2020/9/9 11:16
 */
public interface ConfigService extends BaseService<Config>, EventService {

    /**
     * 保存配置
     *
     * @param paramMap
     * @return
     * @throws GlobalException
     */
    boolean saveConfig(Map<String, String> paramMap) throws GlobalException;

    /**
     * 获取配置
     * @return
     * @throws GlobalException
     */
    Map<String, String> getConfigMap() throws GlobalException;

    /**
     * 通过 key 获取 value
     * @param configKey
     * @return
     * @throws GlobalException
     */
    String getConfigValue(String configKey) throws GlobalException;

    /**
     * 批量保存配置
     * @param configList
     * @throws GlobalException
     */
    void saveConfigBatch(List<Config> configList) throws GlobalException;
}
