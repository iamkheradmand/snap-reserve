package com.snapreserve.snapreserve.service.publisher;

import com.snapreserve.snapreserve.dto.msg.ReservationEvent;

public interface ReservationEventPublisher {
	void publish(ReservationEvent event);
}
