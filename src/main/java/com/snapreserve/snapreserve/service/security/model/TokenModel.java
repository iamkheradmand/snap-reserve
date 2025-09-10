package com.snapreserve.snapreserve.service.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class TokenModel {
	private String accessToken;
	private String refreshToken;
}
