package com.shop.server.dto;

import org.springframework.data.domain.Page;

import com.shop.server.common.constant.ItemSellStatus;

import lombok.Data;

@Data
public class ItemSearchDto {
	/* 상품 검색 조회 전용 DTO (req) */ 
	private String searchDateType;
	
	private ItemSellStatus searchSellStatus;	
	
	private String searchBy;
	
	private String searchQuery = "";
	
	private Page<?> items;
	
}
