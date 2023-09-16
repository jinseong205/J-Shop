package com.shop.server.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shop.server.dto.ItemMainDto;
import com.shop.server.dto.ItemSearchDto;
import com.shop.server.entity.Item;
import com.shop.server.entity.User;

public interface ItemRepositoryCustom {

	Page<Item> getItemManagePage(ItemSearchDto itemSearchDto, Pageable pageable, User user);

	Page<ItemMainDto> getItemMainPage(ItemSearchDto itemSearchDto, Pageable pageable);
	
}
