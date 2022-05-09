package com.light.hexo.core.admin.job;

import com.light.hexo.core.admin.component.DumpService;
import com.light.hexo.common.constant.ConfigEnum;
import com.light.hexo.common.constant.StateEnum;
import com.light.hexo.mapper.model.Backup;
import com.light.hexo.core.admin.service.BackupService;
import com.light.hexo.core.admin.service.ConfigService;
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
        String auto = this.configService.getConfigValue(ConfigEnum.BACKUP_AUTO.getName());
        if (StringUtils.isBlank(auto) || !StateEnum.ON.getCode().equals(Integer.valueOf(auto))) {
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
