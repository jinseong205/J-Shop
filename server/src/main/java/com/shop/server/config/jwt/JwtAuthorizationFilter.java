package com.shop.server.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.server.config.auth.PrincipalDetails;
import com.shop.server.entity.User;
import com.shop.server.error.ErrorResult;
import com.shop.server.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

//Security가 Filter를 가지고 있는데 그 필터 중에 BasicAuthenticationFilter 라는 것이 있음
//권한이나 인증이 필요한 특정 주소를 요청 했을 때 위 필터를 무조건 타게 되어있음
//만약에 권한, 인증이 필요한 주소가 아니라면 해당 필터를 타지 않음


@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter{

	private final UserRepository userRepository;
	
	private final JwtProperties jwtProperties;
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtProperties jwtProperties) {
		super(authenticationManager);
		this.userRepository = userRepository;
		this.jwtProperties = jwtProperties;
	}
	//인증이나 권한이 필요한 주소 요청이 있을 때 해당 필터를 타게 됨
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String jwtHeader = request.getHeader(jwtProperties.getHEADER_STRING());

		//Header 확인
		if(jwtHeader == null || !jwtHeader.startsWith(jwtProperties.getTOKEN_PREFIX())) {
			chain.doFilter(request, response);
			return;
		}

		//JWT 토큰을 검증을 해서 정상적인 사용자인지 확인
		String tokenString = request.getHeader(jwtProperties.getHEADER_STRING()).replace(jwtProperties.getTOKEN_PREFIX(), "");
		
		String username;
		try {
			username = JWT.require(Algorithm.HMAC512(jwtProperties.getSECRET())).build().verify(tokenString).getClaim("username").asString();
		}catch (TokenExpiredException e) {
			ErrorResult errorResult = new ErrorResult("토큰이 만료되었습니다.");
			
			ObjectMapper mapper = new ObjectMapper();
			String result ="";
			try {result = mapper.writeValueAsString(errorResult);} 
			catch (JsonProcessingException ex) {ex.printStackTrace();}
			
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			try {response.getWriter().write(result);} 
			catch (IOException ex) {ex.printStackTrace();}
            return;
        }
		// Jwt 토큰 서명을 통해서 서명이 정상적이면 Authentication 객체를 만들어 준다.
		if(username != null) {
			User userEntity = userRepository.findByUsername(username);
			
			PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
			Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

			//강제로 Security session에 접근하여 Authentication 객체를 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
		}
		chain.doFilter(request, response);
		
	}
	

	
	
}
