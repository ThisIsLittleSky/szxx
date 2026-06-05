package com.szxx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.szxx.mapper")
public class SzxxApplication {
    public static void main(String[] args) {
        SpringApplication.run(SzxxApplication.class, args);
    }
}
