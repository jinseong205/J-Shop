package com.shop.server.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.shop.server.custom.WithMockCustomUser;
import com.shop.server.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@SpringBootTest	
@Transactional
public class UserTest {

	@Autowired
	UserRepository userRepository;

	@PersistenceContext
	EntityManager em;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Test
	@DisplayName("--- Auditing 테스트 ---")
	@WithMockCustomUser
	public void auditingTest() {

		User newUser = User.builder().username("gomawoomi").password(bCryptPasswordEncoder.encode("0000")).name("정진성")
				.email("gomawoomi@gmail.com").roles("ROLE_USER").build();
 
		userRepository.save(newUser);

		em.flush();
		em.clear();

		
		User user = userRepository.findById(newUser.getId()).orElseThrow(EntityNotFoundException::new);

		System.out.println("crtDt : " + user.getCrtDt());
		System.out.println("updtDt : " + user.getUpdtDt());
		System.out.println("create By : " + user.getCrtName());
		System.out.println("modify By : " + user.getUpdtName());

	}

}
