package com.snapreserve.snapreserve.lisener.mapper;

import com.snapreserve.snapreserve.dto.msg.ReservationEvent;
import com.snapreserve.snapreserve.service.reservation.model.PersistReserveModel;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ReservationListenerMapper {

	PersistReserveModel toPersistReserveModel(ReservationEvent message);

}
