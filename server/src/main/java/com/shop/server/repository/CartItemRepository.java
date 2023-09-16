package com.shop.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shop.server.dto.CartDetailDto;
import com.shop.server.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{
	
	CartItem findByCartIdAndItemId(Long cartId, Long ItemId);

	
	@Query("select new com.shop.server.dto.CartDetailDto(ci.id, i.itemName, i.price, ci.count, im.imgUrl) "
			+ "from CartItem ci, ItemImg im "
			+ "join ci.item i "
			+ "where ci.cart.id = :cartId "
			+ "and im.item.id = ci.item.id "
			+ "and im.repImgYn = 'Y' "
			+ "order by ci.crtDt desc")
	List<CartDetailDto> findCartDetailDtoList(Long cartId);
}
