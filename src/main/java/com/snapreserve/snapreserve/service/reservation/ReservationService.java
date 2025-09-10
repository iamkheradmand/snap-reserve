package com.snapreserve.snapreserve.service.reservation;

import com.snapreserve.snapreserve.service.reservation.model.DeleteReserveModel;
import com.snapreserve.snapreserve.service.reservation.model.PersistReserveModel;

public interface ReservationService {

	void persistReservation(PersistReserveModel model);

	void deleteReservation(DeleteReserveModel model);

}
