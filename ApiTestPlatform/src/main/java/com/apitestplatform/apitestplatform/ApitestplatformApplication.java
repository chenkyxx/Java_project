package com.apitestplatform.apitestplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.apitestplatform.dao")
@ComponentScan(basePackages = {"com.apitestplatform.webController","com.apitestplatform.service",
"com.apitestplatform.apitestplatform"})
@EntityScan(basePackages = "com.apitestplatform.entity")
public class ApitestplatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApitestplatformApplication.class, args);
    }

}
