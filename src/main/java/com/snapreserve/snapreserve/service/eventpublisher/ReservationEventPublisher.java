package com.snapreserve.snapreserve.service.eventpublisher;

import com.snapreserve.snapreserve.model.msg.ReservationEvent;

public interface ReservationEventPublisher {
	void publish(ReservationEvent event);
}
