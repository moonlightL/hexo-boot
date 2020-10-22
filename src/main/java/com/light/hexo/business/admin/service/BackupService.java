package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.Backup;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;
import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: BackupService
 * @ProjectName hexo-boot
 * @Description: 备份 Service
 * @DateTime 2020/9/8 18:43
 */
public interface BackupService extends BaseService<Backup> {

    /**
     * 保存备份
     * @param sqlData
     * @return
     * @throws GlobalException
     */
    Backup saveBackup(String sqlData) throws GlobalException;

    /**
     * 批量删除
     * @param idStrList
     * @throws GlobalException
     */
    void removeBackupBatch(List<String> idStrList) throws GlobalException;

    /**
     * 保存备份配置
     * @param configMap
     * @throws GlobalException
     */
    void saveConfig(Map<String, String> configMap) throws GlobalException;
}
