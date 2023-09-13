package com.shop.server.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shop.server.auth.AuthenticationFilter;
import com.shop.server.auth.PrincipalDetailsService;
import com.shop.server.common.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
	
	private final PrincipalDetailsService principalDetailsService;
	private final JwtService jwtService;
	private final ObjectPostProcessor<Object> objectPostProcessor;

	@Bean
	public PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		
		authProvider.setUserDetailsService(principalDetailsService);
		authProvider.setPasswordEncoder(bCryptPasswordEncoder());
		return authProvider;
	}

    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(principalDetailsService).passwordEncoder(bCryptPasswordEncoder());
        return auth.build();
    }
    
	@Bean
    public AuthenticationFilter customAuthenticationFilter() throws Exception {
        
        AuthenticationFilter customAuthenticationFilter = new AuthenticationFilter(jwtService);
        AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(objectPostProcessor);
        customAuthenticationFilter.setAuthenticationManager(authenticationManager(builder));
        return customAuthenticationFilter;
    }

}
