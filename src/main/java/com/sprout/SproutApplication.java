package com.sprout;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 新芽启动类
 *
 * @author sprout
 */
@SpringBootApplication
@MapperScan("com.sprout.**.mapper")
public class SproutApplication {

    public static void main(String[] args) {
        SpringApplication.run(SproutApplication.class, args);
        System.out.println("""
                ============================================
                  (♥◠‿◠)  新芽 sprout-admin 启动成功
                  接口文档: http://localhost:8080/doc.html
                ============================================
                """);
    }
}
