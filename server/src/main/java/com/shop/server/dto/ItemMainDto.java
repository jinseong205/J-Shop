package com.shop.server.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;

@Data
public class ItemMainDto {
	/* 상품 조회 전용 DTO (res) */
	private Long id;
	
	private String itemName;
	
	private String itemDetail;
	
	private String imgUrl;
	
	private Integer price;

	@QueryProjection
	public ItemMainDto(Long id, String itemName, String itemDetail, String imgUrl, Integer price) {
		this.id = id;
		this.itemName = itemName;
		this.itemDetail = itemDetail;
		this.imgUrl = imgUrl;
		this.price = price;
	}
	
	
}
