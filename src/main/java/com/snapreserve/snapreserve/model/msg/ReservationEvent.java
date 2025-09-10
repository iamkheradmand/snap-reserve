package com.snapreserve.snapreserve.model.msg;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReservationEvent implements Serializable {
	private String userName;
	private Long slotId;
}
