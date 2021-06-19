package com.needle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // Enable auditing annotations on entities
@SpringBootApplication
public class EmailSchedulerPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailSchedulerPocApplication.class, args);
	}

}
