package com.snapreserve.snapreserve.service.security.impl;

import com.snapreserve.snapreserve.repository.user.UserRepository;
import com.snapreserve.snapreserve.repository.user.Users;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = userRepository.findByUsername(username).orElseThrow(() -> new
				UsernameNotFoundException("User details not found for the user: " + username));
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
		return new User(user.getUsername(), user.getPassword(), authorities);
	}
}