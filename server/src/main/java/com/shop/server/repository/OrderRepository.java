package com.shop.server.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shop.server.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

	@Query("select o from Order o " + "where o.user.id = :id " + "order by o.crtDt desc")
	List<Order> findOrders(@Param("id") Long id, Pageable pageable);

	@Query("select count(o) from Order o " + "where o.user.id = :id")
	Long countOrder(@Param("id") Long id);
	
}
