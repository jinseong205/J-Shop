package com.shop.server.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Getter Setter
public class UserFormDto {
	/* 회원 가입 전용 DTO (req) */

	@NotBlank(message = "아이디를 입력해주세요.")
	@Length(min=8, max=20, message = "아이디는 8자 이상 20자 이하로 입력하세요.")
	private String username;	
	
	@NotBlank(message = "비밀번호는를 입려해주세요.")
	@Length(min=8, max=20, message = "패스워드는 8자 이상 20자 이하로 입력하세요.")

	private String password;	
	
	private String name;
	
	private String email;
	
	@NotBlank(message = "주소를 입력해주세요.")
	private String addr;


}
