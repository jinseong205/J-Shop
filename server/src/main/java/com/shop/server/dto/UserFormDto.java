package com.shop.server.dto;

import com.shop.server.exception.CustomException;
import com.shop.server.exception.ExceptionCode;

import lombok.Data;

@Data // Getter Setter
public class UserFormDto {
	/* 회원 가입 전용 DTO (req) */

	private String username;

	private String password;

	private String name;

	private String email;

	private String addr;

	public void validate() {
		if (this.username == null || this.username.length() < 8 || username.length() > 20) 
			throw new CustomException(ExceptionCode.VALIDATION_USER_ID);

		if (this.password == null || this.password.length() < 8 || password.length() > 20) 
			throw new CustomException(ExceptionCode.VALIDATION_USER_PASSWORD);

		if (this.addr == null || this.addr.equals("")) 
			throw new CustomException(ExceptionCode.VALIDATION_USER_ADDR);
	}

}
