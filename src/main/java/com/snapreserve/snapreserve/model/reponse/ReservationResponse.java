package com.snapreserve.snapreserve.model.reponse;

import com.snapreserve.snapreserve.model.base.BaseResponse;
import lombok.Data;

@Data
public class ReservationResponse extends BaseResponse {

    private String slotDate;
}
