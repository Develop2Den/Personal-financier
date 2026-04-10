package com.D2D.personal_financier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PersonalFinancierApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalFinancierApplication.class, args);
	}

}
