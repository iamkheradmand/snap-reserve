package com.snapreserve.snapreserve.service.security;


import com.snapreserve.snapreserve.service.security.model.RefreshTokenModel;
import com.snapreserve.snapreserve.service.security.model.TokenModel;
import com.snapreserve.snapreserve.service.security.model.UserDetailsModel;

public interface AuthService {
	TokenModel login(UserDetailsModel userDetails);

	TokenModel register(UserDetailsModel userDetails);

	TokenModel refreshToken(RefreshTokenModel model);

}
