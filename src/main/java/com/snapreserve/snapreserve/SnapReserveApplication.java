package com.snapreserve.snapreserve;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT5M")
public class SnapReserveApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnapReserveApplication.class, args);
	}


}
