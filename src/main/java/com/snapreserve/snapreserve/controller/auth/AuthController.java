package com.snapreserve.snapreserve.controller.auth;

import com.snapreserve.snapreserve.controller.auth.mapper.AuthControllerMapper;
import com.snapreserve.snapreserve.dto.reponse.AuthResponse;
import com.snapreserve.snapreserve.dto.request.LoginRequest;
import com.snapreserve.snapreserve.dto.request.RefreshTokenRequest;
import com.snapreserve.snapreserve.dto.request.RegisterRequest;
import com.snapreserve.snapreserve.service.security.AuthService;
import com.snapreserve.snapreserve.service.security.model.TokenModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "(Production environment only), Please Check active profile!")
@AllArgsConstructor
public class AuthController {

    private final AuthControllerMapper mapper;

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenModel tokenModel = authService.login(mapper.toUserDetailsModel(loginRequest));
        return ResponseEntity.ok(mapper.toAuthResponse(tokenModel));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest loginRequest) {
        TokenModel tokenModel = authService.register(mapper.toUserDetailsModel(loginRequest));
        return ResponseEntity.ok(mapper.toAuthResponse(tokenModel));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        TokenModel tokenModel = authService.refreshToken(mapper.toRefreshTokenModel(refreshTokenRequest));
        return ResponseEntity.ok(mapper.toAuthResponse(tokenModel));
    }
}
