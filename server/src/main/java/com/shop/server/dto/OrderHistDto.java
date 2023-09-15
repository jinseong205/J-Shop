package com.shop.server.dto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.shop.server.common.constant.OrderStatus;
import com.shop.server.entity.Order;

import lombok.Data;

@Data
public class OrderHistDto {
	/* 상품 조회 전용 DTO (res) */
	public OrderHistDto(Order order) {
		this.orderId = order.getId();
		this.orderDate = order.getCrtDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		this.orderStatus = order.getOrderStatus();
	}
	
	private Long orderId;
	private String orderDate;
	private OrderStatus orderStatus;	
	private List<OrderItemDto> orderItemDtoList = new ArrayList<>();
	
	public void addOrderItemDto(OrderItemDto orderItemDto) {
		orderItemDtoList.add(orderItemDto);
	}
	
}
