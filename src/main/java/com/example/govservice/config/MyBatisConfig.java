package com.example.govservice.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan("com.example.govservice.mapper")
public class MyBatisConfig {
    
    // MyBatis配置已在application.yml中完成
    // 这里可以添加额外的MyBatis配置，如分页插件等
}
