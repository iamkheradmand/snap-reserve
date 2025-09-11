package com.snapreserve.snapreserve.controller.reservation;

import com.snapreserve.snapreserve.controller.reservation.mapper.ReservationControllerMapper;
import com.snapreserve.snapreserve.dto.base.BaseResponse;
import com.snapreserve.snapreserve.dto.reponse.DeleteReservationResponse;
import com.snapreserve.snapreserve.dto.request.ReservationRequest;
import com.snapreserve.snapreserve.service.reservation.ReservationService;
import com.snapreserve.snapreserve.service.manager.ReservationManagerService;
import com.snapreserve.snapreserve.service.manager.model.ReserveResultModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/reservation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reservation Management", description = "APIs for creating and managing reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationManagerService processorService;

    private final ReservationService reservationService;

    private final ReservationControllerMapper mapper;

    @Operation(summary = "Create a new reservation", description = "Allocates next available time slot for user and returns reservation details")
    @PostMapping
    public ResponseEntity<BaseResponse> reserve(@RequestBody @Valid ReservationRequest request) {
        log.info("received reservation request {}", request);
        ReserveResultModel reserveResultModel = processorService.doReserve(mapper.toReserveModel(request));
        return ResponseEntity.ok(mapper.toReservationResponse(reserveResultModel));
    }

    @Operation(summary = "Delete existing reservation", description = "Frees up the reserved time slot")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteReservation(@PathVariable("id") String reservationId) {
        log.info("going to delete reservation : {}", reservationId);
        reservationService.deleteReservation(mapper.toDeleteReserveModel(reservationId));
        return ResponseEntity.ok().build();
    }

}
