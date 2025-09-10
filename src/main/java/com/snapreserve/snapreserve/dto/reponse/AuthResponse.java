package com.snapreserve.snapreserve.dto.reponse;

import com.snapreserve.snapreserve.dto.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AuthResponse extends BaseResponse {
	private String accessToken;
	private String refreshToken;
}
