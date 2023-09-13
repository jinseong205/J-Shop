package com.shop.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;
import com.shop.server.entity.User;

import com.shop.server.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor

public class UserService {
	private final UserRepository userRepository;
	
	@Transactional
	public User join(User user) {
		validateUser(user);
		return userRepository.save(user);
	}

	public void validateUser(User user) {

		User findUser = userRepository.findByUsername(user.getUsername()).get();

		if(findUser != null)
			throw new CustomException(ExceptionCode.DUPLICATE_EMAIL_USER_TO_CREATE);
	}
	
	
	@Transactional(readOnly = true)
	public Page<User> getUsers(Pageable pageable){

		Page<User> users = userRepository.findAll(pageable);
		for(User u : users) 
			u.setPassword(null);
		return users;
	}
	
	@Transactional
	public void updateUserRoles(User user) throws Exception{

		User savedUser = userRepository.findById(user.getId()).orElseThrow(() -> new Exception("해당 회원을 찾을 수 없습니다."));
		savedUser.setRoles(user.getRoles());

		userRepository.save(savedUser);
		return;
	}
	
	
}
