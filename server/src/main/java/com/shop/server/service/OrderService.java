package com.shop.server.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.server.auth.PrincipalDetails;
import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;
import com.shop.server.dto.OrderDto;
import com.shop.server.dto.OrderHistDto;
import com.shop.server.dto.OrderItemDto;
import com.shop.server.entity.Item;
import com.shop.server.entity.ItemImg;
import com.shop.server.entity.Order;
import com.shop.server.entity.OrderItem;
import com.shop.server.entity.User;
import com.shop.server.repository.ItemImgRepository;
import com.shop.server.repository.ItemRepository;
import com.shop.server.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

	private final ItemRepository itemRepository;
	private final OrderRepository orderRepository;
	private final ItemImgRepository itemImgRepository;

	@Transactional
	public Long order(OrderDto orderItemDto, PrincipalDetails principalDetails) throws Exception {
		Item item = itemRepository.findById(orderItemDto.getItemId())
				.orElseThrow(() -> new CustomException(ExceptionCode.NO_ITEM_TO_GET));

		List<OrderItem> orderItemList = new ArrayList<>();

		OrderItem orderItem = OrderItem.createOrderItem(item, orderItemDto.getCount());
		orderItemList.add(orderItem);

		Order order = Order.createOrder(principalDetails.getUser(), orderItemList);
		orderRepository.save(order);

		return order.getId();
	}

	@Transactional(readOnly = true)
	public Page<OrderHistDto> getOrderList(PrincipalDetails principalDetails, Pageable pageable) {
		List<Order> orders = orderRepository.findOrders(principalDetails.getUser().getId(), pageable);
		Long totalCount = orderRepository.countOrder(principalDetails.getUser().getId());

		List<OrderHistDto> orderHistDtos = new ArrayList<>();

		for (Order order : orders) {
			OrderHistDto orderHistDto = new OrderHistDto(order);
			List<OrderItem> orderItems = order.getOrderItems();
			for (OrderItem orderItem : orderItems) {
				ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
				OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
				orderHistDto.addOrderItemDto(orderItemDto);
			}
			orderHistDtos.add(orderHistDto);
		}

		return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
	}

	@Transactional(readOnly = true)
	public boolean validateOrder(Long orderId, PrincipalDetails principalDetails) throws Exception {

		Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException(ExceptionCode.NO_ORDER_TO_GET));

		User savedUser = order.getUser();

		if (!principalDetails.getUsername().equals(savedUser.getUsername())) {
			return false;
		}
		return true;
	}

	@Transactional
	public void cancelOrder(Long orderId) throws Exception {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException(ExceptionCode.NO_ORDER_TO_GET));
		order.cancelOrder();
	}

	public Long orders(List<OrderDto> createOrderDtoList, User user) throws Exception {
		
		List<OrderItem> orderItemList = new ArrayList();
		for(OrderDto orderDto : createOrderDtoList) {
			Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(() -> new CustomException(ExceptionCode.NO_ITEM_TO_GET));;
			OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
			orderItemList.add(orderItem);		
		}
		
		Order order = Order.createOrder(user, orderItemList);
		orderRepository.save(order);			
		
		return order.getId();
	}
}
