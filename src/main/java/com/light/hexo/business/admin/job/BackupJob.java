package com.light.hexo.business.admin.job;

import com.light.hexo.business.admin.component.DumpService;
import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.constant.StateEnum;
import com.light.hexo.business.admin.model.Backup;
import com.light.hexo.business.admin.service.BackupService;
import com.light.hexo.business.admin.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: BackupJob
 * @ProjectName hexo-boot
 * @Description: 备份定时器
 * @DateTime 2020/9/9 10:57
 */
@Component
@Slf4j
public class BackupJob {

    @Autowired
    private ConfigService configService;

    @Autowired
    private DumpService dumpService;

    @Autowired
    private BackupService backupService;

    /**
     * 每周 0 点执行
     */
    @Scheduled(cron = "0 0 0 * *  MON")
    public void backupData() {

        // 备份开关
        Map<String, String> configMap = this.configService.getConfigMap();
        String auto = configMap.get(ConfigEnum.BACKUP_AUTO.getName());
        if (!StateEnum.ON.getCode().equals(Integer.valueOf(auto))) {
            log.info("=======自动备份未开启=========");
            return;
        }

        log.info("=======自动备份任务开始=========");

        String sqlData = this.dumpService.getSqlData();
        if (StringUtils.isBlank(sqlData)) {
            return;
        }

        Backup backup = this.backupService.saveBackup(sqlData);

        log.info("=======自动备份任务结束 filePath: {}=========", backup.getFilePath());
    }
}
