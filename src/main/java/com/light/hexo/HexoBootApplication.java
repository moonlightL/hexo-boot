package com.light.hexo;

import com.light.hexo.common.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@Slf4j
@EnableAsync
@EnableCaching
@MapperScan(basePackages = {"com.light.hexo.business.admin.mapper"})
@ServletComponentScan(basePackages= {"com.light.hexo.business.portal.web.filter"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, FlywayAutoConfiguration.class, MybatisAutoConfiguration.class})
public class HexoBootApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HexoBootApplication.class);
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(HexoBootApplication.class, args);
        log.info("访问地址：http://{}:{}/", IpUtil.getHostIp(), context.getEnvironment().getProperty("server.port"));
        log.info("后台登录地址：http://{}:{}/admin/login.html", IpUtil.getHostIp(), context.getEnvironment().getProperty("server.port"));
    }

}
