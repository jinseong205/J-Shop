package com.shop.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shop.server.dto.UserFormDto;
import com.shop.server.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	@PostMapping("api/login")
	public ResponseEntity<?> authenticate(@RequestBody @Valid UserFormDto userFormDto) {
		return new ResponseEntity<>(authService.login(userFormDto), HttpStatus.OK);
	}
}
