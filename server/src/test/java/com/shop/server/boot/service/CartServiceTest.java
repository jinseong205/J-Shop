package com.shop.server.boot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.shop.server.auth.PrincipalDetails;
import com.shop.server.common.constant.ItemSellStatus;
import com.shop.server.dto.CartItemDto;
import com.shop.server.dto.UserFormDto;
import com.shop.server.entity.CartItem;
import com.shop.server.entity.Item;
import com.shop.server.entity.User;
import com.shop.server.repository.CartItemRepository;
import com.shop.server.repository.ItemRepository;
import com.shop.server.repository.UserRepository;
import com.shop.server.service.CartService;

@SpringBootTest
@Transactional
public class CartServiceTest {

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CartService cartService;

	@Autowired
	CartItemRepository cartItemRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	public Item saveItem() {

		Item item = new Item();
		item.setItemName("테스트 상품");
		item.setPrice(10000);
		item.setItemDetail("테스트 상품 상세 설명");
		item.setItemSellStatus(ItemSellStatus.SELL);
		item.setStockNum(100);
		return itemRepository.save(item);

	}

	public User saveUser() {

		UserFormDto userDto = new UserFormDto();

		userDto.setUsername("gomawoomi");
		userDto.setPassword("0000");
		userDto.setName("정진성");
		userDto.setEmail("goamwoomi@gmail.com");
		;

		User user = User.builder().username(userDto.getUsername())
				.password(bCryptPasswordEncoder.encode(userDto.getPassword())).name(userDto.getName())
				.email(userDto.getEmail()).roles("ROLE_USER").build();

		return userRepository.save(user);
	}

	@Test
	@DisplayName("--- 장바구니 생성 테스트 ---")
	public void addCart() throws Exception {	
		Item item = saveItem();
		User user = saveUser();
		
		CartItemDto cartItemDto = new CartItemDto();
		cartItemDto.setCount(5);
		cartItemDto.setItemId(item.getId());
		
		Long cartItemId = cartService.addCart(cartItemDto, new PrincipalDetails(user));
		
		CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new Exception("장바구니 정보를 찾을 수 없습니다."));
		
		assertEquals(item.getId(), cartItem.getItem().getId());
		assertEquals(cartItemDto.getCount(), cartItem.getCount());
		
				
	}

}
