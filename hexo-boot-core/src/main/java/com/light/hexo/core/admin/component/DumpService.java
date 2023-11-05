package com.light.hexo.core.admin.component;

import com.light.hexo.common.constant.ConfigEnum;
import com.light.hexo.common.constant.HexoExceptionEnum;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.util.DateUtil;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.core.admin.service.ConfigService;
import com.light.hexo.mapper.config.HikariProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author MoonlightL
 * @ClassName: DumpService
 * @ProjectName hexo-boot
 * @Description: 备份工具
 * @DateTime 2020/9/8 18:36
 */
@Component
@Slf4j
public class DumpService {

    @Autowired
    private HikariProperties hikariProperties;

    @Autowired
    private ConfigService configService;

    public String dumpData() throws GlobalException  {

        String dirPath = this.configService.getConfigValue(ConfigEnum.BACKUP_DIR.getName());
        if (StringUtils.isBlank(dirPath)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_BACKUP_DIR_NOT_EXIST);
        }

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String backupName = String.format("backup_%s.sql", DateUtil.ldtToStr(LocalDateTime.now(), DateTimeFormatter.ofPattern("yyyyMMddHHmm")));

        String url = hikariProperties.getJdbcUrl();
        String username = hikariProperties.getUsername();
        String password = hikariProperties.getPassword();

        // 获取数据库名称
        String tmp = url.substring(0,url.indexOf("?"));
        String dbName = tmp.substring(url.lastIndexOf("/") + 1);

        // 判断系统
        String systemName = System.getProperty("os.name");
        String dumpCmd = systemName.toUpperCase().contains("WINDOWS") ? "mysqldump.exe" : "mysqldump";

        // 执行命令
        boolean backupResult = this.executeCommand(dumpCmd +
                " -u" + username +
                (StringUtils.isNotBlank(password) ? (" -p" + password) : "") +
                " --ignore-table=" + dbName + ".t_message " +
                " --ignore-table=" + dbName + ".t_ext_visit_info " +
                " --ignore-table=" + dbName + ".t_ext_visit_detail " +
                " --result-file=" + dir.getAbsolutePath() + File.separator + backupName +
                " --databases " + dbName
        );

        log.info("============= dumpData 备份结果:{} ===============", backupResult);

        return backupName;
    }

    private boolean executeCommand(String command) throws GlobalException {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            if (e.getMessage().contains("mysqldump")) {
                ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_BACKUP_COMMAND_NOT_EXIST);
            }
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

}