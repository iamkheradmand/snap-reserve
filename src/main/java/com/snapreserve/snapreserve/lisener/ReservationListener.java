package com.snapreserve.snapreserve.lisener;

import com.snapreserve.snapreserve.lisener.mapper.ReservationListenerMapper;
import com.snapreserve.snapreserve.model.msg.ReservationEvent;
import com.snapreserve.snapreserve.service.reservationpersistance.ReservationPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationListener {

	private final ReservationPersistenceService persistReservation;

	private final ReservationListenerMapper mapper;

	@RabbitListener(queues = "reservation.queue", containerFactory = "rabbitListenerContainerFactory")
	public void receiveMessage(ReservationEvent event) {
		log.info("[ReservationListener] - Received message reservation.queue {}", event);
		try {
			persistReservation.persistReserve(mapper.toPersistReserveModel(event));
		} catch (Exception e) {
			log.info("[ReservationListener] - an exception happen during message persistence : {}", event);
			throw new AmqpRejectAndDontRequeueException("Failed to persist", e);
		}
	}
}
