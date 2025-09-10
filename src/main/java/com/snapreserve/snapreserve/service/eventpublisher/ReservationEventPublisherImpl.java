package com.snapreserve.snapreserve.service.eventpublisher;


import com.snapreserve.snapreserve.config.ConfigProvider;
import com.snapreserve.snapreserve.dto.msg.ReservationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationEventPublisherImpl implements ReservationEventPublisher {

	private final RabbitTemplate rabbitTemplate;

	private final ConfigProvider configProvider;

	@Override
	public void publish(ReservationEvent event) {
		try {
			rabbitTemplate.convertAndSend(configProvider.getReservationExchange(), configProvider.getReservationRoutingKey(), event);
		} catch (Exception exception) {
			throw new RuntimeException("an error happen, please try again");
		}
	}

}
