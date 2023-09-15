package com.shop.server.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemDto {
	/* 장부구니 생성 전용 DTO (req) */
	@NotNull(message = "상품 아이디는 필수 입력 값입니다.")
	private Long itemId;
	
	@Min(value = 1 ,message = "chlth 1개 이상 담아주세요")
	private int count;
}
