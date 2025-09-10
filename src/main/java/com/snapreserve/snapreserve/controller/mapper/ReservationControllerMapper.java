package com.snapreserve.snapreserve.controller.mapper;

import com.snapreserve.snapreserve.model.reponse.ReservationResponse;
import com.snapreserve.snapreserve.model.request.ReservationRequest;
import com.snapreserve.snapreserve.service.reservationrequest.model.ReserveModel;
import com.snapreserve.snapreserve.service.reservationrequest.model.ReserveResultModel;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ReservationControllerMapper {

	ReserveModel toReserveModel(ReservationRequest request);

	ReservationResponse toReservationResponse(ReserveResultModel model);

}
