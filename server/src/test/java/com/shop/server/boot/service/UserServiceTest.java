package com.shop.server.boot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.shop.server.dto.UserFormDto;
import com.shop.server.entity.User;
import com.shop.server.service.UserService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class UserServiceTest {

	@Autowired
	UserService userService;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public User createUser() {
		UserFormDto userDto = new UserFormDto();
		
		userDto.setUsername("gomawoomi");
		userDto.setPassword("0000");
		userDto.setName("정진성");
		userDto.setEmail("goamwoomi@gmail.com");;
		
		return User.builder()
				.username(userDto.getUsername())
				.password(bCryptPasswordEncoder.encode(userDto.getPassword()))
				.name(userDto.getName())
				.email(userDto.getEmail())
				.roles("ROLE_USER")
				.build();	
	}
	
	@Test
	@DisplayName("--- 회원가입 테스트 ---")
	public void saveMemberTest() {
		
		User user = createUser();
		User savedUser = userService.join(user);

		assertEquals(user.getUsername(), savedUser.getUsername());
		assertEquals(user.getPassword(), savedUser.getPassword());
		assertEquals(user.getName(), savedUser.getName());
		assertEquals(user.getEmail(), savedUser.getEmail());
	}
}
