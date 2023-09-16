package com.shop.server.dto;

import java.util.List;

import lombok.Data;

@Data
public class CartOrderDto {
	/* 장바구니 주문 전용 DTO (req) */
	private Long cartItemId;
	private List<CartOrderDto> cartOrderList;
}
