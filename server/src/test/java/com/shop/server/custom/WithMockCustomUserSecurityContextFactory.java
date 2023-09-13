package com.shop.server.custom;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
		
		final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

		final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				annotation.userName(), null, List.of(new SimpleGrantedAuthority(annotation.role())));
		securityContext.setAuthentication(authenticationToken);
		
		
		return securityContext;
	}
}