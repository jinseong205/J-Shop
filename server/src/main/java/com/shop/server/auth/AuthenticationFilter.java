package com.shop.server.auth;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.server.common.jwt.JwtService;
import com.shop.server.dto.TokenDto;
import com.shop.server.dto.UserFormDto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtService jwtService;

    public AuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // 요청에서 로그인 정보를 파싱
        	UserFormDto userFormDto = new ObjectMapper().readValue(request.getInputStream(), UserFormDto.class);

            // 로그인 정보로 UsernamePasswordAuthenticationToken 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
            		userFormDto.getUsername(),
            		userFormDto.getPassword()
            );

            // AuthenticationManager를 사용하여 인증 시도
            return this.getAuthenticationManager().authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //로그인 성공 시, JWT 토큰을 생성하고 응답에 포함
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        String jwtToken = jwtService.generateToken(principalDetails);

        TokenDto token = TokenDto.builder().accessToken(jwtToken).build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(token));
    }
    
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		// 로그인 실패시, 오류메세지
		AuthErrorResult authErrorResult = new AuthErrorResult("해당 유저 정보를 찾을 수 없습니다.");
		
		ObjectMapper mapper = new ObjectMapper();
		String result = mapper.writeValueAsString(authErrorResult);
		
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
		response.getWriter().write(result);
	}

}