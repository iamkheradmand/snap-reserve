package com.snapreserve.snapreserve.service.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RefreshTokenModel {
	private String userName;
	private String refreshToken;
}
