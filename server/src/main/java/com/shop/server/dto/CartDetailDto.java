package com.shop.server.dto;

import lombok.Data;

@Data
public class CartDetailDto {
	/* 장바구니 조회 전용 DTO (res) */
	private Long cartItemId;
	private String itemName;
	private int price;
	private int count;
	private String imgUrl;
	
	public CartDetailDto(Long cartItemId, String itemName, int price, int count, String imgUrl) {
		super();
		this.cartItemId = cartItemId;
		this.itemName = itemName;
		this.price = price;
		this.count = count;
		this.imgUrl = imgUrl;
	}
	
}
