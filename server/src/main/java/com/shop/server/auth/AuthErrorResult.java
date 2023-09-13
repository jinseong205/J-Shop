package com.shop.server.auth;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AuthErrorResult {
	private String message;

	public AuthErrorResult(String message) {
		this.message = message;
	}
	
	
}
