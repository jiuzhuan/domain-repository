package com.github.jiuzhuan.domain.repository.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.github.jiuzhuan.domain.repository")
@SpringBootApplication
public class DomainRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DomainRepositoryApplication.class, args);
    }

}
