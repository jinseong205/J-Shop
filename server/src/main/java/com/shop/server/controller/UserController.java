package com.shop.server.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.shop.server.auth.PrincipalDetails;
import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;
import com.shop.server.dto.UserFormDto;
import com.shop.server.entity.User;
import com.shop.server.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@PostMapping("/api/join")
	public ResponseEntity<?> join(@RequestBody @Valid UserFormDto userFormDto, BindingResult bindingResult)
			throws Exception {

		if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bindingResult.getFieldError().getDefaultMessage());
		}
	
		User user = User.builder().username(userFormDto.getUsername())
				.password(bCryptPasswordEncoder.encode(userFormDto.getPassword())).name(userFormDto.getName())
				.email(userFormDto.getEmail()).addr(userFormDto.getAddr()).roles("ROLE_USER").build();
		userService.join(user);
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	@GetMapping("/api/admin/users")
	public ResponseEntity<?> users( Optional<Integer> page,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception {

		Pageable pegealbe = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
		
		Page<User> users = userService.getUsers(pegealbe);
		return new ResponseEntity<>(users, HttpStatus.OK);
	}


	@PatchMapping("/api/admin/user/roles")
	public ResponseEntity<?> user( @RequestBody User user ,@AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception {
		
		if (!principalDetails.getUser().getRoleList().contains("ROLE_ADMIN")
				&& !principalDetails.getUser().getRoleList().contains("ROLE_ADMIN")) 
			throw new CustomException(ExceptionCode.PERMISSION_ERROR);

		userService.updateUserRoles(user);
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	

}
