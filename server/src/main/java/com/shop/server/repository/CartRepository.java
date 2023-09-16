package com.shop.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.server.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{
	Cart findByUserId(Long userId);
}
