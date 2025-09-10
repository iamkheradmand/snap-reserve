package com.snapreserve.snapreserve.service.reservationrequest;

import com.snapreserve.snapreserve.service.reservationrequest.model.ReserveModel;
import com.snapreserve.snapreserve.service.reservationrequest.model.ReserveResultModel;

public interface ReservationRequestService {

	ReserveResultModel doReserve(ReserveModel model);

}
