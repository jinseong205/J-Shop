package com.shop.server.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.server.auth.PrincipalDetails;
import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;
import com.shop.server.dto.CartDetailDto;
import com.shop.server.dto.CartItemDto;
import com.shop.server.dto.CartOrderDto;
import com.shop.server.dto.OrderDto;
import com.shop.server.entity.Cart;
import com.shop.server.entity.CartItem;
import com.shop.server.entity.Item;
import com.shop.server.entity.User;
import com.shop.server.repository.CartItemRepository;
import com.shop.server.repository.CartRepository;
import com.shop.server.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

	private final ItemRepository itemRepository;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final OrderService orderService;

	public Long addCart(CartItemDto cartItemDto, PrincipalDetails principalDetails) throws Exception {

		Item item = itemRepository.findById(cartItemDto.getItemId())
				.orElseThrow(() -> new CustomException(ExceptionCode.NO_ITEM_TO_GET));

		Cart cart = cartRepository.findByUserId(principalDetails.getUser().getId());

		if(cart==null) {
			cart = Cart.createCart(principalDetails.getUser());
			cartRepository.save(cart);
			}
		
		CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

		if (savedCartItem != null) {
			savedCartItem.addCount(cartItemDto.getCount());
			return savedCartItem.getId();
		} else {
			CartItem cartItem = CartItem.createCatrtItem(cart, item, cartItemDto.getCount());
			cartItemRepository.save(cartItem);
			return cartItem.getId();
		}
	}

	@Transactional(readOnly = true)
	public List<CartDetailDto> getCartList(PrincipalDetails principalDetails) throws Exception {

		List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

		Cart cart = cartRepository.findByUserId(principalDetails.getUser().getId());

		if (cart == null)
			return cartDetailDtoList;

		cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
		return cartDetailDtoList;

	}

	public boolean validateCartItem(Long cartItemId, PrincipalDetails principalDetails) throws Exception {
		CartItem cartItem = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new CustomException(ExceptionCode.NO_CART_TO_GET));
		User savedUser = cartItem.getCart().getUser();

		if (principalDetails.getUser().getUsername().equals(savedUser.getUsername()))
			return true;
		return false;
	}

	public void updateCartItem(Long cartItemId, int count) throws Exception {
		CartItem cartItem = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new CustomException(ExceptionCode.NO_CART_TO_GET));
		cartItem.updateCount(count);
		return;
	}

	public void deleteCartItem(Long cartItemId) throws Exception {
		CartItem cartItem = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new CustomException(ExceptionCode.NO_CART_TO_GET));
		cartItemRepository.delete(cartItem);
		return;
	}

	public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, PrincipalDetails principalDetails) throws Exception {

		List<OrderDto> orderDtoList = new ArrayList<>();
		for (CartOrderDto cartOrderDto : cartOrderDtoList) {
			CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
					.orElseThrow(() -> new CustomException(ExceptionCode.NO_CART_TO_GET));
			
			OrderDto orderDto = new OrderDto();
			orderDto.setItemId(cartItem.getItem().getId());
			orderDto.setCount(cartItem.getCount());
			orderDtoList.add(orderDto);	
			}

		Long orderId = orderService.orders(orderDtoList, principalDetails.getUser());
		
		for(CartOrderDto cartOrderDto : cartOrderDtoList) {
			CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
					.orElseThrow(() -> new CustomException(ExceptionCode.NO_CART_TO_GET));
			cartItemRepository.delete(cartItem);				
		}
		
		return orderId;	
	}

}
