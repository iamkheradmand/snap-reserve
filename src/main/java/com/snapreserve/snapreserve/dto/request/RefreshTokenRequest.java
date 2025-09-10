package com.snapreserve.snapreserve.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RefreshTokenRequest {
	private String userName;
	private String refreshToken;
}

