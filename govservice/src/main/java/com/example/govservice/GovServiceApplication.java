package com.example.govservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 政务服务平台启动类
 */
@SpringBootApplication
@MapperScan("com.example.govservice.mapper")
public class GovServiceApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(GovServiceApplication.class, args);
            System.out.println("=================================");
            System.out.println("政务服务平台启动成功！");
            System.out.println("访问地址: http://localhost:8080/api");
            System.out.println("测试接口: http://localhost:8080/api/test/hello");
            System.out.println("=================================");
        } catch (Exception e) {
            System.err.println("应用启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
/**启动：】进cmd运行：
 * cd C:\Users\86189\Desktop\project\multi-ethnic-government-platform
 * npm run dev
 ```**/