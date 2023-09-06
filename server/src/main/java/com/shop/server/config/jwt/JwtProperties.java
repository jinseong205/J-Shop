package com.shop.server.config.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class JwtProperties {
	
	@Value("${jwt.token.secret}")
	private String SECRET;
	
	@Value("${jwt.token.expiry-seconds}")
	private int EXPIRATION_TIME; //  1/1000초
	
	private String TOKEN_PREFIX = "Bearer ";
	private String HEADER_STRING = "Authorization";
}
