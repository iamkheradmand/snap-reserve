package com.snapreserve.snapreserve.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {
	private String email;
	private String userName;
	private String password;
}
