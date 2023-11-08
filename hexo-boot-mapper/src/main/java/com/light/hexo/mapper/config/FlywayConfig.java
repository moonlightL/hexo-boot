//package com.light.hexo.mapper.config;
//
//import org.flywaydb.core.Flyway;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//
//import javax.annotation.PostConstruct;
//
///**
// * @Author MoonlightL
// * @ClassName: FlywayConfig
// * @ProjectName hexo-boot
// * @Description: flyway 配置
// * @DateTime 2020/9/18 10:21
// */
//@Configuration
////@DependsOn("dataSourceConfig")
//public class FlywayConfig {
//
//    @Autowired
//    private HikariProperties hikariProperties;
//
//    @PostConstruct
//    public void migrate() {
//        Flyway flyway = Flyway.configure()
//                .dataSource(hikariProperties.getJdbcUrl(), hikariProperties.getUsername(), hikariProperties.getPassword())
//                .cleanDisabled(true)
//                .baselineOnMigrate(true)
//                .baselineVersion("0")
//                .locations("db/migration")
//                .load();
//        flyway.migrate();
//    }
//}
