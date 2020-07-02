package com.crongze;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.crongze.mapper")
public class DrawCardApplication {
    public static void main(String[] args) {
        SpringApplication.run(DrawCardApplication.class, args);
    }
}
