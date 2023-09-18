package com.shop.server.common.test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.shop.server.entity.User;
import com.shop.server.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataGenerator {

	private final UserRepository userRepository;
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void generateData() {
    	createAdminUser();
    	createUser();
    }
    

    private void createAdminUser(){
		User user = User.builder().username("admin")
				.password(bCryptPasswordEncoder.encode("testtest")).name("관리자")
				.email("admin@admin.com").addr("서울 특별시 ...").roles("ROLE_ADMIN").build();
		userRepository.save(user);
		}

    private void createUser(){
		User user = User.builder().username("testtest")
				.password(bCryptPasswordEncoder.encode("testtest")).name("사용자")
				.email("testtest@test.com").addr("서울 특별시 ...").roles("ROLE_USER").build();
		userRepository.save(user);
    }
}
