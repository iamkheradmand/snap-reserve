package com.snapreserve.snapreserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SnapReserveApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnapReserveApplication.class, args);
	}

}
