package com.shop.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.server.entity.ItemImg;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long>{

	List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

	ItemImg findByItemIdAndRepImgYn(Long itemId, String repimgYn);
	
}
