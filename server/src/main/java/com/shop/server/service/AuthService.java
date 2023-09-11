package com.shop.server.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.shop.server.auth.PrincipalDetails;
import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;
import com.shop.server.common.jwt.JwtService;
import com.shop.server.dto.Token;
import com.shop.server.dto.UserFormDto;
import com.shop.server.entity.User;
import com.shop.server.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	
	public Token login(@Valid UserFormDto userFormDto) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(userFormDto.getUsername(), userFormDto.getPassword()));
		
		
		User user = userRepository.findByUsername(userFormDto.getUsername()).orElseThrow(() -> new CustomException(ExceptionCode.NO_USER_TO_GET));
		PrincipalDetails principalDetails = new PrincipalDetails(user);
		String jwtToken = jwtService.generateToken(principalDetails);
  
		return Token.builder().accessToken(jwtToken).build();
	}
}
