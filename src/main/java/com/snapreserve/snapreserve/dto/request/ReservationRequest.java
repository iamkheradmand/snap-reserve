package com.snapreserve.snapreserve.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReservationRequest {

    @NotBlank
    private String userName;

}
