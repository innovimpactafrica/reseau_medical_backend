package com.example.rml.back_office_rml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //Automatise la mise à jour des slots (expiration si la date passe)
public class BackOfficeRmlApplication {

	public static void main(String[] args) {

		SpringApplication.run(BackOfficeRmlApplication.class, args);
		System.out.println("BackOfficeRmlApplication");

	}

}
