package com.light.hexo.business.admin.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.datasource.druid.url}")
    private String url;

    @Value("${spring.datasource.druid.username}")
    private String username;

    @Value("${spring.datasource.druid.password}")
    private String password;

    public String getSqlData() {

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

    private String execCommand(String command) {
        InputStream in = null;
        Process process = null;
        String result = null;
        try {
            process = Runtime.getRuntime().exec(command);
            in = process.getInputStream();
            result = IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
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