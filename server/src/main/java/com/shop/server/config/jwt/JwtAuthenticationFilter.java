	package com.shop.server.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.server.config.auth.PrincipalDetails;
import com.shop.server.entity.User;
import com.shop.server.exception.auth.AuthErrorResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private final AuthenticationManager authenticationManager;
	
	private final JwtProperties jwtProperties;
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtProperties jwtProperties) {
	    super();
		super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login", "POST"));
		super.setAuthenticationManager(authenticationManager);;
		this.authenticationManager = authenticationManager;
		this.jwtProperties = jwtProperties;
	}


	// Login 요청시로그인 시도를 위해서 실행 되는 함수
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		log.debug("********** JwtAuthenticationFilter > attemptAuthentication() ***********");
		
		try {
			// 1. username, password 
			ObjectMapper om = new ObjectMapper();
			User user = om.readValue(request.getInputStream(), User.class);
			log.debug("********** user : " + user.toString() + " **********");
			
			// 2. 정상인지 로그인 시도. 유저정보로 토큰을 만들어 authenticationManager로 로그인 시도
			//    -> PrincipalDetailsService 의 loadUserByUser가 실행됨 
			//    -> 정상이면  authentication 리턴 됨
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
			
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			
			PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
			
			log.debug("********** login Success - principalDetails -> user : " + principalDetails.getUser().toString() + "**********");
		
			
			// 3. principDetails를 세션에 저장 => 로그인이 됨 (Spring Security가 권한 관리를 용이하게 함)
			return authentication;	
			
		}catch(NullPointerException e) {
			log.debug("********** login Fail **********");
			
			AuthErrorResult errorResult = new AuthErrorResult("존재하지 않는 아이디 입니다.");
			
			ObjectMapper mapper = new ObjectMapper();
			String result ="";
			try {result = mapper.writeValueAsString(errorResult);} 
			catch (JsonProcessingException ex) {ex.printStackTrace();}
			
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			try {response.getWriter().write(result);} 
			catch (IOException ex) {ex.printStackTrace();}
		}catch(BadCredentialsException e) {
			log.debug("********** login Fail **********");
			
			AuthErrorResult errorResult = new AuthErrorResult("아이디 혹은 패스워드가 일치하지 않습니다.");
			
			ObjectMapper mapper = new ObjectMapper();
			String result ="";
			try {result = mapper.writeValueAsString(errorResult);} 
			catch (JsonProcessingException ex) {ex.printStackTrace();}
			
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			try {response.getWriter().write(result);} 
			catch (IOException ex) {ex.printStackTrace();}
		}catch (Exception e) {
			log.debug("********** login Fail **********");
			
			e.printStackTrace();
			
			AuthErrorResult errorResult = new AuthErrorResult(e.getMessage());
			
			ObjectMapper mapper = new ObjectMapper();
			String result ="";
			try {result = mapper.writeValueAsString(errorResult);} 
			catch (JsonProcessingException ex) {ex.printStackTrace();}
			
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			try {response.getWriter().write(result);} 
			catch (IOException ex) {ex.printStackTrace();}
		}
		
		return null;
	}
	
	
	//attemptAuthentication 실행후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행됨
	//JWT 토큰을 만들어서 requset 요청한 사용자에게 jwt 토큰을 response에 넣어주면됨.
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		log.debug("********** login Success - successfulAuthentication **********");
		PrincipalDetails principalDetails = (PrincipalDetails)authResult.getPrincipal();
		
		//Hash 암호방식
		String jwtToken = JWT.create()
				.withSubject("demo")																	//Token 이름
				.withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getEXPIRATION_TIME()))					//Token 만료 시간
				.withClaim("id", principalDetails.getUser().getId())
				.withClaim("username", principalDetails.getUser().getUsername())
				.withClaim("roles", principalDetails.getUser().getRoles())
				.sign(Algorithm.HMAC512(jwtProperties.getSECRET()));


		
		response.addHeader(jwtProperties.getHEADER_STRING(), jwtProperties.getTOKEN_PREFIX() + jwtToken);
		log.debug("********** JWT Token - " + jwtToken + " **********");
		
	}


	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {

		log.debug("********** login Fail - unsuccessfulAuthentication **********");
		
		AuthErrorResult errorResult = new AuthErrorResult("아이디 혹은 패스워드가 일치하지 않습니다.");
		
		ObjectMapper mapper = new ObjectMapper();
		String result = mapper.writeValueAsString(errorResult);
		
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().write(result);
	}

	


}
