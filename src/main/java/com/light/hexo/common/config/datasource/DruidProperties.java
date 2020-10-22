package com.light.hexo.common.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Properties;

/**
 * @Author MoonlightL
 * @ClassName: DruidProperties
 * @ProjectName hexo-boot
 * @Description: 数据源属性
 * @DateTime 2020/9/18 10:48
 */
@Component
@Data
@ConfigurationProperties(prefix = "spring.datasource.druid")
public class DruidProperties {

    private String driverClassName;

    private String url;

    private String username;

    private String password;

    public int maxActive;

    public int initialSize;

    public long maxWait;

    public int minIdle;

    public long timeBetweenEvictionRunsMillis;

    public long minEvictableIdleTimeMillis;

    public String validationQuery;

    public boolean testWhileIdle;

    public boolean testOnBorrow;

    public boolean testOnReturn;

    public boolean poolPreparedStatements;

    public String maxOpenPreparedStatements;

    public boolean useGlobalDataSourceStat;

    public Properties connectionProperties;

    public String filters;

    public DruidDataSource dataSource(DruidDataSource datasource) {

        // 配置数据源基础参数
        datasource.setDriverClassName(driverClassName);
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);

        // 配置初始化大小、最小、最大
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);

        // 配置获取连接等待超时的时间
        datasource.setMaxWait(maxWait);

        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        // 配置一个连接在池中最小、最大生存的时间，单位是毫秒
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

        // 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果 validationQuery 为 null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用
        datasource.setValidationQuery(validationQuery);

        // 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效
        datasource.setTestWhileIdle(testWhileIdle);

        // 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        datasource.setTestOnBorrow(testOnBorrow);

        // 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        datasource.setTestOnReturn(testOnReturn);

        datasource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);

        datasource.setConnectProperties(connectionProperties);

        try {
            datasource.setFilters(filters);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return datasource;
    }
}
