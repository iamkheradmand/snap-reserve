package com.snapreserve.snapreserve.controller.reservation;

import com.snapreserve.snapreserve.controller.reservation.mapper.ReservationControllerMapper;
import com.snapreserve.snapreserve.dto.base.BaseResponse;
import com.snapreserve.snapreserve.dto.reponse.DeleteReservationResponse;
import com.snapreserve.snapreserve.dto.request.ReservationRequest;
import com.snapreserve.snapreserve.service.reservation.ReservationService;
import com.snapreserve.snapreserve.service.reservationrequest.ReservationRequestService;
import com.snapreserve.snapreserve.service.reservationrequest.model.ReserveResultModel;
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
@RequestMapping(value = "api/reservation/v1/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationRequestService reservationRequestService;

    private final ReservationService reservationPersistenceService;

    private final ReservationControllerMapper mapper;

    /*
    curl --location --request POST 'http://localhost:8080/api/reservation/v1/' \
            --header 'Content-Type: application/json' \
            --data-raw '{
            "userName":"iamkheradmand"
            }'
*/

    @PostMapping
    public ResponseEntity<BaseResponse> reserve(@RequestBody @Valid ReservationRequest request) {
        log.info("Received reservation request {}", request);
        ReserveResultModel reserveResultModel = reservationRequestService.doReserve(mapper.toReserveModel(request));
        return ResponseEntity.ok(mapper.toReservationResponse(reserveResultModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteReservation(@PathVariable("id") String reservationId) {
        log.info("going to delete reservation : {}", reservationId);
        reservationPersistenceService.deleteReservation(mapper.toDeleteReserveModel(reservationId));
        return ResponseEntity.ok(new DeleteReservationResponse());
    }

}
