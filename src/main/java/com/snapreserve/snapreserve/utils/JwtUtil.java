package com.snapreserve.snapreserve.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class JwtUtil {

	@Value("${snap-reserve.jwt.secret}")
	private final String secret;

	@Value("${snap-reserve.access.token.expiration}")
	private final long accessExpiration; // 15 minutes

	@Value("${snap-reserve.refresh.token.expiration}")
	private final long refreshExpiration; // 7 days

	public String extractUserName(String username) {
		return extractClaim(username, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateAccessToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, username, accessExpiration);
	}

	public String generateRefreshToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		//claims.put
		return createToken(claims, username, refreshExpiration);
	}

	private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public Boolean validateToken(String token, String email) {
		final String username = extractUserName(token);
		return (username.equals(email) && !isTokenExpired(token));
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
}
