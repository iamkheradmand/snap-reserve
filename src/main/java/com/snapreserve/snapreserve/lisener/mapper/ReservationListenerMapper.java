package com.snapreserve.snapreserve.lisener.mapper;

import com.snapreserve.snapreserve.model.msg.ReservationEvent;
import com.snapreserve.snapreserve.service.reservationpersistance.model.PersistReserveModel;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ReservationListenerMapper {

	PersistReserveModel toPersistReserveModel(ReservationEvent message);

}
