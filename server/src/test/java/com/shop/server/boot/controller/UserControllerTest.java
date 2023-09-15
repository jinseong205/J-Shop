package com.shop.server.boot.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shop.server.dto.UserFormDto;
import com.shop.server.entity.User;
import com.shop.server.service.UserService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

	@Autowired
	private UserService userService;

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public User createUser(String username, String email, String password) {
		
		UserFormDto userFormDto = new UserFormDto();
		
		userFormDto.setUsername(username);
		userFormDto.setEmail(email);
		userFormDto.setPassword(password);
		userFormDto.setName("테스트");
		userFormDto.setAddr("경기도 군포시 오금동");;
		
		User user;
		user = User.builder()
				.username(userFormDto.getUsername())
				.password(bCryptPasswordEncoder.encode(userFormDto.getPassword()))
				.name(userFormDto.getName())
				.email(userFormDto.getAddr())
				.addr(userFormDto.getAddr())
				.roles("ROLE_USER")
				.build();
		
		
		return userService.join(user);
	}
	
	@Test
	@DisplayName("--- 로그인 성공 테스트 ---")
	public void loginSuccessTest() throws Exception{
		
		String username = "gogo1234";
		String email = "gogo123@gmail.com";
		String password = "12341234!!";
		
		this.createUser(username, email, password);
	
		
		
		UserFormDto user = new UserFormDto();
		user.setUsername(username);
		user.setPassword(password);
		
		String json = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(user);
		
		mockMvc.perform(//URL REQ
					MockMvcRequestBuilders.post("http://localhost:8080/api/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json)
				).andExpect(//기댓값
					MockMvcResultMatchers.status().isOk()
				).andDo(
					MockMvcResultHandlers.print()
				);
	}
}

