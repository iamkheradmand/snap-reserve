package com.snapreserve.snapreserve.controller.auth.mapper;


import com.snapreserve.snapreserve.dto.reponse.AuthResponse;
import com.snapreserve.snapreserve.dto.request.LoginRequest;
import com.snapreserve.snapreserve.dto.request.RefreshTokenRequest;
import com.snapreserve.snapreserve.dto.request.RegisterRequest;
import com.snapreserve.snapreserve.service.security.model.RefreshTokenModel;
import com.snapreserve.snapreserve.service.security.model.TokenModel;
import com.snapreserve.snapreserve.service.security.model.UserDetailsModel;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AuthControllerMapper {

	UserDetailsModel toUserDetailsModel(LoginRequest loginRequest);

	UserDetailsModel toUserDetailsModel(RegisterRequest loginRequest);

	AuthResponse toAuthResponse(TokenModel tokenModel);

	RefreshTokenModel toRefreshTokenModel(RefreshTokenRequest refreshTokenRequest);
}