package com.snapreserve.snapreserve.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;

	private final PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();
		String password = authentication.getCredentials().toString();

		UserDetails user = userDetailsService.loadUserByUsername(email);

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException("Invalid email or password");
		}

		return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}