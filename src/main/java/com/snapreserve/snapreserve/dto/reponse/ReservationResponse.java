package com.snapreserve.snapreserve.dto.reponse;

import com.snapreserve.snapreserve.dto.base.BaseResponse;
import lombok.Data;

@Data
public class ReservationResponse extends BaseResponse {

    private String reservationId;

    private String reservationDate;
}
