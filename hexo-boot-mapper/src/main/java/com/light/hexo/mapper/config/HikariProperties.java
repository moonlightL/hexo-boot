package com.light.hexo.mapper.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: MoonlightL
 * @ClassName: HikariProperties
 * @ProjectName: hexo-boot
 * @Description:
 * @DateTime: 2022-05-02 10:12
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.datasource.hikari")
public class HikariProperties {

    private String driverClassName;

    private String jdbcUrl;

    private String username;

    private String password;

    /**
     * 连接池中允许的最小连接数。缺省值：10
     */
    private Integer minimumIdle;

    /**
     * 连接池中允许的最大连接数。缺省值：10
     */
    private Integer maximumPoolSize;

    /**
     * 一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，建议设置比数据库超时时长少30秒
     */
    private Integer maxLifetime;

    /**
     * 一个连接idle状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
     */
    private Integer idleTimeout;

    /**
     * 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
     */
    private Integer connectionTimeout;

    /**
     * 数据库连接测试语句
     */
    private String connectionTestQuery;

    private String poolName;

    public HikariDataSource createDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMinimumIdle(minimumIdle);
        dataSource.setMaximumPoolSize(maximumPoolSize);
        dataSource.setMaxLifetime(maxLifetime);
        dataSource.setPoolName(poolName);
        dataSource.setIdleTimeout(idleTimeout);
        dataSource.setConnectionTimeout(connectionTimeout);
        dataSource.setConnectionTestQuery(connectionTestQuery);
        return dataSource;
    }
}
