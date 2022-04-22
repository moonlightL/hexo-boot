package com.light.hexo.common.plugin;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSessionFactory;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginLoader;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.nio.file.Path;

/**
 * @Author MoonlightL
 * @ClassName: AbstractPluginManager
 * @ProjectName hexo-boot
 * @Description: 抽象插件管理器
 * @DateTime 2022/4/20, 0020 14:37
 */
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
            SqlSessionFactory sqlSessionFactory = this.getApplicationContext().getBean(SqlSessionFactory.class);
            ScriptRunner scriptRunner = new ScriptRunner(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource().getConnection());
            scriptRunner.setLogWriter(null);
            scriptRunner.runScript(Resources.getResourceAsReader(classLoader, sqlFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
