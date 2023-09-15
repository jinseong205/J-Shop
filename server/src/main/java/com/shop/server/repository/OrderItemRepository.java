package com.shop.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.server.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

}
