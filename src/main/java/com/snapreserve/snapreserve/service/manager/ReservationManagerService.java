package com.snapreserve.snapreserve.service.manager;

import com.snapreserve.snapreserve.service.manager.model.ReserveModel;
import com.snapreserve.snapreserve.service.manager.model.ReserveResultModel;

public interface ReservationManagerService {

	ReserveResultModel doReserve(ReserveModel model);

}
