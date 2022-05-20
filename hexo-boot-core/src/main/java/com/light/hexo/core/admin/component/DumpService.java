package com.light.hexo.core.admin.component;

import com.light.hexo.common.constant.HexoExceptionEnum;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.mapper.config.HikariProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

    public String getSqlData() throws GlobalException  {

        String url = hikariProperties.getJdbcUrl();

        // 获取数据库名称
        String tmp = url.substring(0,url.indexOf("?"));
        String dbName = tmp.substring(url.lastIndexOf("/") + 1);

        // 判断系统
        String systemName = System.getProperty("os.name");
        String dumpCmd;
        if (systemName.toUpperCase().contains("WINDOWS")) {
            dumpCmd = "mysqldump.exe";
        } else {
            dumpCmd = "mysqldump";
        }

        String username = hikariProperties.getUsername();
        String password = hikariProperties.getPassword();

        // 执行命令获取 sql 内容
        String sqlStr;
        if (StringUtils.isBlank(password)) {
            sqlStr = this.execCommand(dumpCmd + " -u" + username + " --databases " + dbName);
        } else {
            sqlStr = this.execCommand(dumpCmd + " -u" + username + " -p" + password + " --databases " + dbName);
        }

        if (StringUtils.isBlank(sqlStr)) {
            log.info("导出 SQL 文件失败【sql 内容为空】");
            return sqlStr;
        }

        return sqlStr;
    }

    private String execCommand(String command) throws GlobalException {
        InputStream in = null;
        Process process = null;
        String result = null;
        try {
            process = Runtime.getRuntime().exec(command);
            in = process.getInputStream();
            result = IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            if (e.getMessage().contains("mysqldump")) {
                ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_BACKUP_COMMAND_NOT_EXIST);
            }
        } finally {
            if (process != null) {
                process.destroy();
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}