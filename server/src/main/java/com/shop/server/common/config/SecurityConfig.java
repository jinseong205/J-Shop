package com.shop.server.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.shop.server.auth.AuthenticationFilter;
import com.shop.server.common.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	private final AuthenticationProvider authenticationProvider;
	
	private final AuthenticationFilter authenticationFilter;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(
						(sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests((authorize) -> authorize
				        .requestMatchers(new AntPathRequestMatcher("/api/user/**")).hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER")
				        .requestMatchers(new AntPathRequestMatcher("/api/manager/**")).hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
						.requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasAnyAuthority("ROLE_ADMIN")
						
						.anyRequest().permitAll())
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilter(authenticationFilter);
		return http.build();
	}
}