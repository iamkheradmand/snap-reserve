package com.snapreserve.snapreserve.dto.msg;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReservationEvent implements Serializable {
    private String reservationId;
    private String userName;
    private Long slotId;
}
