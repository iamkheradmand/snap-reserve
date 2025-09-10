package com.snapreserve.snapreserve.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class ConfigProvider {

	private final Environment env;

	public String getApplicationName() {
		return env.getProperty("spring.application.name");
	}

	public String getReservationQueue() {
		return env.getRequiredProperty("rabbit.reservation.queue");
	}

	public String getReservationExchange() {
		return env.getRequiredProperty("rabbit.reservation.exchange");
	}

	public String getReservationRoutingKey() {
		return env.getRequiredProperty("rabbit.reservation.routingkey");
	}
}
