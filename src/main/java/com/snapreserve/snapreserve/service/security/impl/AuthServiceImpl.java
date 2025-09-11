package com.snapreserve.snapreserve.service.security.impl;

import com.snapreserve.snapreserve.repository.user.UserRepository;
import com.snapreserve.snapreserve.repository.user.Users;
import com.snapreserve.snapreserve.service.security.AuthService;
import com.snapreserve.snapreserve.service.security.model.RefreshTokenModel;
import com.snapreserve.snapreserve.service.security.model.TokenModel;
import com.snapreserve.snapreserve.service.security.model.UserDetailsModel;
import com.snapreserve.snapreserve.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final JwtUtil jwtUtil;

	private final AuthenticationManager authenticationManager;

	@Override
	public TokenModel login(UserDetailsModel model) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(model.getUserName(), model.getPassword()));
		return createToken(model.getUserName());
	}

	@Override
	public TokenModel register(UserDetailsModel model) {
		userRepository.findByUsername(model.getUserName())
				.ifPresent(myUser -> {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "User with userName " + myUser.getUsername() + " already exists.");
				});
		model.setPassword(passwordEncoder.encode(model.getPassword()));
		Users newUser = Users.builder()
				.username(model.getUserName())
				.email(model.getEmail())
				.password(model.getPassword())
				.build();
		userRepository.save(newUser);
		return createToken(model.getUserName());
	}

	@Override
	public TokenModel refreshToken(RefreshTokenModel model) {
		if (!jwtUtil.validateToken(model.getRefreshToken(), model.getUserName())) {
			throw new RuntimeException("Invalid or expired refresh token");
		}
		String username = jwtUtil.extractUserName(model.getRefreshToken());
		Users userDetails = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		return createToken(userDetails.getUsername());
	}

	private TokenModel createToken(String username) {
		return TokenModel.builder()
				.accessToken(jwtUtil.generateAccessToken(username))
				.refreshToken(jwtUtil.generateRefreshToken(username))
				.build();
	}

}
