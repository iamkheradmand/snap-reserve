package com.snapreserve.snapreserve.service.reservationpersistance;

import com.snapreserve.snapreserve.service.reservationpersistance.model.PersistReserveModel;

public interface ReservationPersistenceService {

	void persistReserve(PersistReserveModel model);

}
