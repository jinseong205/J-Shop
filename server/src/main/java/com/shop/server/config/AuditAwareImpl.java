package com.shop.server.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.shop.server.config.auth.PrincipalDetails;

public class AuditAwareImpl implements AuditorAware<String>{

	@Override
	public Optional<String> getCurrentAuditor() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		
		String username = "";
	    if (authentication != null) {
	        Object principal = authentication.getPrincipal();
	        if (principal instanceof PrincipalDetails) {
	            PrincipalDetails principalDetails = (PrincipalDetails) principal;
	            username = principalDetails.getUser().getUsername();
	        } else if (principal instanceof String) {
	            username = (String) principal;
	        }
	    }
	    

		return Optional.of(username);
		
	}
	
}
