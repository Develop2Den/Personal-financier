package com.d2d.personal_financier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class PersonalFinancierApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalFinancierApplication.class, args);
    }

}
