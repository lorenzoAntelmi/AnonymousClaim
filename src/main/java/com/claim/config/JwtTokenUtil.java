package com.claim.config;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 *
 * @author Lorenzo Antelmi
 * This Class is responsible for performing JWT operations
 * like creation and validation.
 *
 * 
 */

@Component
public class JwtTokenUtil implements Serializable {

	/**
	 * Why is as SerialVersionUID used?
	 */
	private static final long serialVersionUID = -5113663546936926051L;

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	@Value("${jwt.secret}")
	private String secret;



	/**
	 * This part causes an Error
	 * Error creating bean with name 'jwtTokenUtil': Injection of autowired dependencies failed; nested exception is
	 * java.lang.IllegalArgumentException: Could not resolve placeholder 'jwt.secret' in value "${jwt.secret}"$
	 *
	@Value("${jwt.secret}")
	**/


		//for retrieving any information from token we will need the secret key
		private Claims getAllClaimsFromToken(String token) {
			return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
			return claimsResolver.apply(claims);
	}

		//retrieve username from jwt token
		public String getUsernameFromToken(String token) {
			return getClaimFromToken(token, Claims::getSubject);
	}

		//retrieve expiration date from jwt token - Maybe Java Date.sql is needed to import?
		public Date getExpirationDateFromToken(String token) {
			return getClaimFromToken(token, Claims::getExpiration);
	}

		//check if the token has expired
		private Boolean isTokenExpired(String token) {
			final Date expiration = getExpirationDateFromToken(token);
			return expiration.before(new Date());
	}

		//while creating the token -
		//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
		//2. Sign the JWT using the HS512 algorithm and secret key.
		//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
		//   compaction of the JWT to a URL-safe string
		private String doGenerateToken(Map<String, Object> claims, String subject) {

			return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
					.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

		//generate token for user
		public String generateToken(UserDetails userDetails) {
			Map<String, Object> claims = new HashMap<>();
			return doGenerateToken(claims, userDetails.getUsername());
	}

		//validate token
		public Boolean validateToken(String token, UserDetails userDetails) {
			final String username = getUsernameFromToken(token);
			return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

}
