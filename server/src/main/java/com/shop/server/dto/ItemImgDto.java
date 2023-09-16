package com.shop.server.dto;

import org.modelmapper.ModelMapper;

import com.shop.server.entity.ItemImg;

import lombok.Data;

@Data
public class ItemImgDto {
	/* 상품 생성,수정 DTO (req/res)*/
	private Long Id;
	
	private String imgName;
	
	private String oriImgName;
	
	private String imgUrl;
	
	private String repImgYn;
	
	private	static ModelMapper modelMapper = new ModelMapper();
	
	public static ItemImgDto of(ItemImg itemImg) {
		return modelMapper.map(itemImg, ItemImgDto.class);
	}
}
