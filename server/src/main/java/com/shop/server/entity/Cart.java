package com.shop.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="cart")
@Data
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	@Column(name="cart_id")
	private long id;
	
	@OneToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	public static Cart createCart(User user) {
		Cart cart = new Cart();
		cart.setUser(user);
		return cart;
	}


}
