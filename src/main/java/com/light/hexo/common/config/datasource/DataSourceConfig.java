package com.light.hexo.common.config.datasource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: DataSourceConfig
 * @ProjectName hexo-boot
 * @Description: 配置数据源
 * @DateTime 2020/9/18 10:44
 */
@Configuration
@Primary
public class DataSourceConfig {

    @Autowired
    private DruidProperties druidProperties;

    @Bean
    public DataSource dataSource() {
        DruidDataSource druidDataSource = druidProperties.dataSource(new DruidDataSource());
        List<Filter> filterList = new ArrayList<>();
        filterList.add(wallFilter());
        druidDataSource.setProxyFilters(filterList);

        try {

            Class.forName(druidDataSource.getDriverClassName());
            String datasourceUrl = druidDataSource.getUrl();
            int index = datasourceUrl.indexOf("?");
            String url_01 = datasourceUrl.substring(0, index);
            String url_02 = datasourceUrl.substring(index);
            String baseUrl = url_01.substring(0, url_01.lastIndexOf("/"));

            Connection connection = DriverManager.getConnection(baseUrl + url_02, druidDataSource.getUsername(), druidDataSource.getPassword());
            Statement statement = connection.createStatement();

            // 创建数据库
            String databaseName = url_01.substring(url_01.lastIndexOf("/") + 1);
            statement.executeUpdate("create database if not exists `" + databaseName + "` default character set utf8 COLLATE utf8_general_ci");

            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return druidDataSource;
    }

    /**
     * druid sql 监控访问 servlet
     * @return
     */
    @Bean
    public ServletRegistrationBean druidStatViewServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
        //白名单：
//        servletRegistrationBean.addInitParameter("allow","127.0.0.1");
        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
//        servletRegistrationBean.addInitParameter("deny","192.168.1.73");
        //登录查看信息的账号密码.
//        servletRegistrationBean.addInitParameter("loginUsername","admin");
//        servletRegistrationBean.addInitParameter("loginPassword","123456");
        //是否能够重置数据.
        servletRegistrationBean.addInitParameter("resetEnable","false");
        return servletRegistrationBean;
    }

    /**
     *  druid sql 监控访问过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");
        //添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    @Bean
    public WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig());
        return wallFilter;
    }

    @Bean
    public WallConfig wallConfig() {
        WallConfig config = new WallConfig();
        // 允许一次执行多条语句
        config.setMultiStatementAllow(true);
        // 允许非基本语句的其他语句
        config.setNoneBaseStatementAllow(true);
        return config;
    }
}
