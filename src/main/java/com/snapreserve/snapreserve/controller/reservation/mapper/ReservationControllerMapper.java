package com.snapreserve.snapreserve.controller.reservation.mapper;

import com.snapreserve.snapreserve.dto.reponse.ReservationResponse;
import com.snapreserve.snapreserve.dto.request.ReservationRequest;
import com.snapreserve.snapreserve.service.reservation.model.DeleteReserveModel;
import com.snapreserve.snapreserve.service.reservationrequest.model.ReserveModel;
import com.snapreserve.snapreserve.service.reservationrequest.model.ReserveResultModel;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ReservationControllerMapper {

	ReserveModel toReserveModel(ReservationRequest request);

	ReservationResponse toReservationResponse(ReserveResultModel model);

    DeleteReserveModel toDeleteReserveModel(String reservationId);
}
