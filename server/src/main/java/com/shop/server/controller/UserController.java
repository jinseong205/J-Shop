package com.shop.server.controller;

import java.util.Optional;
import javax.validation.Valid;


import org.springframework.validation.BindingResult;
import com.shop.server.auth.PrincipalDetails;
import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;
import com.shop.server.dto.UserFormDto;
import com.shop.server.entity.User;
import com.shop.server.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@PostMapping("/api/join")
	public ResponseEntity<?> join(@RequestBody UserFormDto userFormDto)
			throws Exception {
		
		userFormDto.validate();
		

		User user = User.builder().username(userFormDto.getUsername())

				.password(bCryptPasswordEncoder.encode(userFormDto.getPassword())).name(userFormDto.getName())
				.email(userFormDto.getEmail()).addr(userFormDto.getAddr()).roles("ROLE_USER").build();
		userService.join(user);
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	@GetMapping("/api/users")
	public ResponseEntity<?> users( Optional<Integer> page,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception {

		Pageable pegealbe = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

		if (!principalDetails.getUser().getRoleList().contains("ROLE_ADMIN")
				&& !principalDetails.getUser().getRoleList().contains("ROLE_ADMIN"))
			throw new CustomException(ExceptionCode.PERMISSION_ERROR);

		Page<User> users = userService.getUsers(pegealbe);
		return new ResponseEntity<>(users, HttpStatus.OK);
	}


	@PatchMapping("/api/user/roles")
	public ResponseEntity<?> user( @RequestBody User user ,@AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception {
		
		if (!principalDetails.getUser().getRoleList().contains("ROLE_ADMIN")
				&& !principalDetails.getUser().getRoleList().contains("ROLE_ADMIN")) 
			throw new CustomException(ExceptionCode.PERMISSION_ERROR);

		userService.updateUserRoles(user);
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	

}
