package com.snapreserve.snapreserve.lisener;

import com.snapreserve.snapreserve.lisener.mapper.ReservationListenerMapper;
import com.snapreserve.snapreserve.dto.msg.ReservationEvent;
import com.snapreserve.snapreserve.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationListener {

	private final ReservationService persistReservation;

	private final ReservationListenerMapper mapper;

	@RabbitListener(queues = "reservation.queue", containerFactory = "rabbitListenerContainerFactory")
	public void receiveMessage(ReservationEvent event) {
		log.info("Received message reservation.queue {}", event);
		try {
			persistReservation.persistReservation(mapper.toPersistReserveModel(event));
		} catch (Exception e) {
			log.info("an exception happen during message persistence : {} - {}", event, e.getMessage());
			throw new AmqpRejectAndDontRequeueException("Failed to persist", e);
		}
	}

	@RabbitListener(queues = "reservation.deadletter.queue")
	public void handleDeadLetterMessage(Message message) {
		String body = new String(message.getBody(), StandardCharsets.UTF_8);
		log.error("Dead Letter: {}", body);

		// Trigger compensation: refund payment, notify user, alert support and etc...
	}
}
