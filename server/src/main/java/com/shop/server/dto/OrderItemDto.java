package com.shop.server.dto;

import com.shop.server.entity.OrderItem;

import lombok.Data;

@Data
public class OrderItemDto {
	/* 상품 조회 전용 DTO (res) */
	public OrderItemDto(OrderItem orderItem, String imgUrl) {
		this.itemName = orderItem.getItem().getItemName();
		this.count = orderItem.getCount();
		this.orderPrice = orderItem.getOrderPrice();
		this.imgUrl = imgUrl;
	}

	private String itemName;
	private int count;
	private int orderPrice;
	private String imgUrl;
	
}
