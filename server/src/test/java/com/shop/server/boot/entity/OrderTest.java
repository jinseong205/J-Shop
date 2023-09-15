package com.shop.server.boot.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.shop.server.common.constant.ItemSellStatus;
import com.shop.server.entity.Item;
import com.shop.server.entity.Order;
import com.shop.server.entity.OrderItem;
import com.shop.server.entity.User;
import com.shop.server.repository.ItemRepository;
import com.shop.server.repository.OrderItemRepository;
import com.shop.server.repository.OrderRepository;
import com.shop.server.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class OrderTest {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderItemRepository orderItemRepository;
	
	@Autowired
	ItemRepository itemRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@PersistenceContext
	EntityManager em;

	@DisplayName("--- 상품 저장 테스트 ---")
	public Item createItem() {

		Item product = new Item();
		product.setItemName("Test Item");
		product.setPrice(10000);
		product.setItemDetail("Test Desc");
		product.setItemSellStatus(ItemSellStatus.SELL);
		product.setStockNum(100);

		return product;
	}

	@DisplayName("--- 오더 생성 테스트 ---")
	public Order createOrder() {
		Order order = new Order();

		for (int i = 0; i < 3; i++) {
			Item item = createItem();
			itemRepository.save(item);
			OrderItem orderItem = new OrderItem();
			orderItem.setItem(item);
			orderItem.setCount(10);
			orderItem.setOrderPrice(1000);
			orderItem.setOrder(order);
			order.getOrderItems().add(orderItem);
		}

		User user = User.builder().username("gomawoomi").password(bCryptPasswordEncoder.encode("0000")).name("정진성")
				.email("gomawoomi@gmail.com").roles("ROLE_USER").build();

		userRepository.save(user);

		order.setUser(user);
		orderRepository.save(order);

		return order;
	}

	@DisplayName("--- 고아 객체 제거 테스트 ---")
	public void cascadeTest() {
		Order order = new Order();

		for (int i = 0; i < 3; i++) {
			Item item = this.createItem();
			itemRepository.save(item);

			OrderItem orderItem = new OrderItem();
			orderItem.setItem(item);
			orderItem.setCount(10);
			orderItem.setOrderPrice(1000);
			orderItem.setOrder(order);
			order.getOrderItems().add(orderItem);
		}

		orderRepository.saveAndFlush(order);
		em.clear();

		Order savedOrder = orderRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);

		assertEquals(3, savedOrder.getOrderItems().size());
	}

	@DisplayName("--- 영속성 전이 테스트 ---")
	public void orphaRemovalTest() {
		Order order = this.createOrder();
		order.getOrderItems().remove(0);
		em.flush();

	}

	@Test
	@DisplayName("--- 지연 로딩 테스트 ---")
	public void lazyLoadingTest() {
		Order order = this.createOrder();
		Long orderItemId = order.getOrderItems().get(0).getId();
		em.flush();
		em.clear();
		
		OrderItem orderItem = orderItemRepository.findById(orderItemId)
								.orElseThrow(EntityNotFoundException::new);
		
		System.out.println("Order class : " + orderItem.getOrder().getClass());
		System.out.println("========================");
		orderItem.getOrder().getCrtDt();
		System.out.println("========================");
		
		
	}

}


