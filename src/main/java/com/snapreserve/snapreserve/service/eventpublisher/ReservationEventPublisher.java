package com.snapreserve.snapreserve.service.eventpublisher;

import com.snapreserve.snapreserve.dto.msg.ReservationEvent;

public interface ReservationEventPublisher {
	void publish(ReservationEvent event);
}
