package com.light.hexo.common.plugin;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.pf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import javax.sql.DataSource;
import java.nio.file.Path;

/**
 * @Author MoonlightL
 * @ClassName: AbstractPluginManager
 * @ProjectName hexo-boot
 * @Description: 抽象插件管理器
 * @DateTime 2022/4/20, 0020 14:37
 */
@Slf4j
public abstract class AbstractPluginManager extends DefaultPluginManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public AbstractPluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public void runSqlFile(ClassLoader classLoader, String sqlFile) {
        try {
            DataSource dataSource = this.getApplicationContext().getBean(DataSource.class);
            ScriptRunner scriptRunner = new ScriptRunner(dataSource.getConnection());
            scriptRunner.setAutoCommit(true);
            scriptRunner.setFullLineDelimiter(false);
            scriptRunner.setDelimiter(";");
            scriptRunner.setSendFullScript(false);
            scriptRunner.setStopOnError(false);
            scriptRunner.setLogWriter(null);
            scriptRunner.runScript(Resources.getResourceAsReader(classLoader, sqlFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
