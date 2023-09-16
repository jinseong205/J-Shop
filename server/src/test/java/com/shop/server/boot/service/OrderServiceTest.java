package com.shop.server.boot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.shop.server.auth.PrincipalDetails;
import com.shop.server.common.constant.ItemSellStatus;
import com.shop.server.common.constant.OrderStatus;
import com.shop.server.dto.OrderDto;
import com.shop.server.dto.UserFormDto;
import com.shop.server.entity.Item;
import com.shop.server.entity.Order;
import com.shop.server.entity.OrderItem;
import com.shop.server.entity.User;
import com.shop.server.repository.ItemRepository;
import com.shop.server.repository.OrderRepository;
import com.shop.server.repository.UserRepository;
import com.shop.server.service.OrderService;

@SpringBootTest
@Transactional
public class OrderServiceTest {

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	UserRepository userRepository;

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

	@DisplayName("--- 주문 생성 테스트 ---")
	public void order() throws Exception {
		Item item = saveItem();
		User user = saveUser();

		OrderDto orderItemDto = new OrderDto();

		orderItemDto.setCount(10);
		orderItemDto.setItemId(item.getId());

		Long orderId = orderService.order(orderItemDto, new PrincipalDetails(user));

		Order order = orderRepository.findById(orderId).orElseThrow(() -> new Exception("해당 주문을 찾을 수 없습니다."));

		List<OrderItem> orderItems = order.getOrderItems();

		int totalPrice = orderItemDto.getCount() * item.getPrice();

		assertEquals(totalPrice, order.getTotalPrice());
	}
	
	@Test
	@DisplayName("--- 주문 취소 테스트 ---")
	public void cancelOrder() throws Exception {
		Item item = saveItem();
		User user = saveUser();
		
		OrderDto orderDto = new OrderDto();
		orderDto.setCount(10);
		orderDto.setItemId(item.getId());
		
		Long orderId = orderService.order(orderDto, new PrincipalDetails(user));
		
		Order order = orderRepository.findById(orderId).orElseThrow(()-> new Exception("주문정보를 찾을 수 없습니다"));
		
		orderService.cancelOrder(orderId);
		
		assertEquals(OrderStatus.CANCEL, order.getOrderStatus());
		assertEquals(100, item.getStockNum());		
		
		
		
	}
		
		
}
