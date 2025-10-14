package com.genc.healthinsurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = "com.genc.healthinsurance") 
@EntityScan(basePackages = "com.genc.healthinsurance") 
@EnableJpaRepositories(basePackages = "com.genc.healthinsurance")
@SpringBootApplication
public class HealthInsuranceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthInsuranceApplication.class, args);
    }
}
